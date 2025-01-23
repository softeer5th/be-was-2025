package webserver.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.header.Cookie;
import webserver.header.SetCookie;
import webserver.interceptor.HandlerInterceptor;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

import java.util.Optional;

/**
 * 쿠키를 기반으로 세션을 식별하는 인터셉터
 */
public class SessionInterceptor implements HandlerInterceptor {
    private static final String COOKIE_NAME = "SID";
    private static final Logger log = LoggerFactory.getLogger(SessionInterceptor.class);
    private final SessionManager sessionManager;

    /**
     * 생성자
     *
     * @param sessionManager 세선을 서버측에서 관리하는 매니저
     */
    public SessionInterceptor(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * 핸들러가 호출되기 전, 쿠키를 통해 세션을 식별하거나 만들어서 세션을 HttpRequest에 설정하는 메서드
     *
     * @param request 클라이언트로부터 받은 요청
     * @param context 미사용
     * @return 세션을 설정한 HttpRequest
     */
    @Override
    public HttpRequest preHandle(HttpRequest request, Context context) {
        // 쿠키에서 세션 ID를 추출
        Optional<String> sessionId = extractSessionId(request);
        HttpSession session = sessionId.flatMap(sessionManager::getSession)
                .orElseGet(sessionManager::createAndSaveSession); // 세션이 없으면 생성

        log.debug("preHandle: Session: {}", session);
        // handler에서 세션을 사용할 수 있게 request에 세션을 설정
        request.setSession(session);

        return request;
    }

    /**
     * 핸들러가 호출된 후, 세션의 상태에 따라 쿠키를 설정하거나 삭제하는 메서드
     *
     * @param request  HTTP 요청
     * @param response HTTP 응답
     * @param context  미사용
     * @return 세션 상태에 따라 Set-Cookie로 쿠키를 설정하거나 삭제한 HttpResponse
     */
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
    private Optional<String> extractSessionId(HttpRequest request) {
        return request.getHeaders().getCookie(COOKIE_NAME).map(Cookie::getValue);
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
