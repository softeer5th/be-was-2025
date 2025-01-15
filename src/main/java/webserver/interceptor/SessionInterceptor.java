package webserver.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;
import webserver.session.HttpSession;
import webserver.session.SessionManager;

// 쿠키를 기반으로 세션을 식별하는 인터셉터
public class SessionInterceptor implements HandlerInterceptor {
    private static final String COOKIE_NAME = "SID";
    private static final Logger log = LoggerFactory.getLogger(SessionInterceptor.class);
    private final SessionManager sessionManager;

    public SessionInterceptor(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public HttpRequest preHandle(HttpRequest request, Context context) {
        // 쿠키에서 세션 ID를 추출
        String sessionId = request.getHeaders().getCookie(COOKIE_NAME);
        if (sessionId == null)
            // 세션 ID가 없으면 postHandle에서 새로운 세션 ID를 Set-Cookie로 보낼 수 있게 context에 저장
            context.set(new SessionData(sessionManager.createSessionId(), true));
        else
            context.set(new SessionData(sessionId, false));

        // 세션 ID로 세션을 가져오거나 새로 생성
        HttpSession session = sessionManager.getSession(sessionId).orElseGet(() -> {
            HttpSession newSession = new HttpSession(sessionId);
            // 새로 생성한 세션을 저장
            sessionManager.saveSession(sessionId, newSession);
            return newSession;
        });
        log.debug("Session: {}", session);
        // handler에서 세션을 사용할 수 있게 request에 세션을 설정
        request.setSession(session);

        return request;
    }

    @Override
    public HttpResponse postHandle(HttpRequest request, HttpResponse response, Context context) {
        // preHandle에서 보낸 데이터
        SessionData data = (SessionData) context.get();
        if (data.shouldSaveCookie) {
            // 새로 생성한 세션 ID를 쿠키로 보냄
            response.setCookie(COOKIE_NAME, data.sessionId);
        }
        return response;
    }

    record SessionData(String sessionId, boolean shouldSaveCookie) {
    }
}
