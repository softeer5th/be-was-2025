package handler.request_handler;

import http.cookie.Cookie;
import http.enums.HttpStatus;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.session.SessionManager;

public class UserLogoutRequestHandler implements RequestHandler{
    private final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return true;
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        Cookie cookie = httpRequest.getCookie("sessionId");

        sessionManager.removeSession(cookie.getValue());

        cookie.setMaxAge(0);

        return new HttpResponse.Builder()
                .httpStatus(HttpStatus.SEE_OTHER)
                .location("http://localhost:8080/")
                .setCookie(cookie)
                .build();
    }
}
