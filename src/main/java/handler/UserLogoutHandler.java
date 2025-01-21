package handler;

import db.SessionDataManager;
import exception.BaseException;
import exception.HttpErrorCode;
import exception.UserErrorCode;
import http.Cookie;
import http.HttpRequestInfo;
import http.HttpResponse;
import http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static http.HttpMethod.POST;

public class UserLogoutHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(UserLogoutHandler.class);

    private final SessionDataManager sessionDataManager;

    public UserLogoutHandler(SessionDataManager sessionDataManager) {
        this.sessionDataManager = sessionDataManager;
    }

    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        logger.info("UserLogoutHandler");

        if (request.getMethod() != POST) {
            throw new BaseException(HttpErrorCode.INVALID_HTTP_METHOD);
        }
        HttpResponse response = new HttpResponse();
        String sid = extractValidSessionId(request);
        sessionDataManager.removeSession(sid);
        logger.debug("User Logged out successfully.");

        response.setStatus(HttpStatus.FOUND);
        response.setCookies(createExpiredSessionCookie());
        response.setHeaders("Location", "/index.html");

        return response;
    }

    private Cookie createExpiredSessionCookie() {
        Cookie cookie = new Cookie("sid", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }

    private String extractValidSessionId(HttpRequestInfo request) {
        Cookie sessionCookie = request.getCookie("sid");

        if (sessionCookie == null) {
            logger.error("No session cookie found in request");
            throw new BaseException(UserErrorCode.MISSING_SESSION);
        }

        String sessionId = sessionCookie.getValue();
        if (sessionId.isEmpty()) {
            logger.error("Session cookie is empty");
            throw new BaseException(UserErrorCode.INVALID_SESSION);
        }

        if (sessionDataManager.findUserBySessionID(sessionId) == null) {
            logger.error("Invalid session ID: sid={}", sessionId);
            throw new BaseException(UserErrorCode.USER_NOT_FOUND_FOR_SESSION);
        }

        return sessionId;
    }
}
