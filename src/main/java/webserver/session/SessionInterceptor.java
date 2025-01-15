package webserver.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.header.SetCookie;
import webserver.interceptor.HandlerInterceptor;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

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
        String sessionId = extractSessionId(request);
        HttpSession session = sessionManager.getSession(sessionId).orElse(null);
        if (sessionId == null || session == null) {
            // 세션 ID가 없거나 session이 만료되었으면 세션을 새로 만든다.
            session = sessionManager.createAndSaveSession();
        }
        log.debug("preHandle: Session: {}", session);
        // handler에서 세션을 사용할 수 있게 request에 세션을 설정
        request.setSession(session);

        return request;
    }

    @Override
    public HttpResponse postHandle(HttpRequest request, HttpResponse response, Context context) {
        HttpSession session = request.getSession();

        switch (session.getState()) {
            case NEW:
                log.debug("postHandle: New session: {}", session);
                setSessionCookie(response, session);
                break;
            case INVALIDATED:
                log.debug("postHandle: Invalidated session: {}", session);
                clearSessionCookie(response, session);
                break;
            case ACTIVE:
                log.debug("postHandle: Active session: {}", session);
                break;
        }
        return response;
    }

    // 쿠키에서 세션 ID를 추출하는 메서드
    private String extractSessionId(HttpRequest request) {
        return request.getHeaders().getCookie(COOKIE_NAME);
    }

    // 세션 ID를 Set-Cookie로 브라우저에 저장하는 메서드
    private void setSessionCookie(HttpResponse response, HttpSession session) {
        response.setCookie(SetCookie.createSessionCookie(COOKIE_NAME, session.getSessionId()));
        // 세션 활성화
        session.active();
    }

    // 세션 ID를 Set-Cookie로 브라우저로 삭제하는 메서드
    private void clearSessionCookie(HttpResponse response, HttpSession session) {
        response.setCookie(SetCookie.expireCookie(COOKIE_NAME));
        sessionManager.removeSession(session.getSessionId());
    }

}
