package handler;

import db.SessionManager;
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


    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        logger.info("UserLogoutHandler");

        if (request.getMethod() != POST) {
            throw new BaseException(HttpErrorCode.INVALID_HTTP_METHOD);
        }
        HttpResponse response = new HttpResponse();
        String sid = request.getCookie("sid").getValue();
        if (SessionManager.findUserBySessionID(sid) == null) {
            logger.error("UserLogoutHandler: Invalid sessionID");
            throw new BaseException(UserErrorCode.USER_NOT_FOUND_FOR_SESSION);
        }

        SessionManager.removeSession(sid);
        logger.debug("User Logged out successfully.");

        response.setStatus(HttpStatus.FOUND);
        response.setCookies(deleteSessionCookie());
        response.setHeaders("Location", "/index.html");

        return response;
    }

    private Cookie deleteSessionCookie(){
        Cookie cookie = new Cookie("sid", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
}
