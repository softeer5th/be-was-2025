package handler;

import db.SessionManager;
import exception.BaseException;
import exception.HttpErrorCode;
import exception.UserErrorCode;
import http.HttpRequestInfo;
import http.HttpResponse;
import http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.QueryUtil;

import java.util.Map;

import static http.HttpMethod.POST;

public class UserLogoutHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(UserLogoutHandler.class);
    private static final int QUERY_SIZE = 1;


    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        logger.info("UserLogoutHandler");
        if (request.getMethod() != POST) {
            throw new BaseException(HttpErrorCode.INVALID_HTTP_METHOD);
        }
        HttpResponse response = new HttpResponse();
        Map<String, String> params = QueryUtil.parseQueryParams(request.getBody(), QUERY_SIZE);
        if( SessionManager.findUserBySessionID(params.get("sessionID")) == null) {
            logger.error("UserLogoutHandler: Invalid sessionID");
            throw new BaseException(UserErrorCode.USER_NOT_FOUND_FOR_SESSION);
        }
        SessionManager.removeSession(params.get("sid"));
        logger.debug("User Logged out successfully.");

        response.setStatus(HttpStatus.FOUND);
        response.setCookies("sid=; Max-Age=0; Path=/; HttpOnly");
        response.setHeaders("Location", "/index.html");

        return response;
    }
}
