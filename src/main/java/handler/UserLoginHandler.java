package handler;

import db.Database;
import db.SessionManager;
import exception.BaseException;
import exception.HttpErrorCode;
import http.Cookie;
import http.HttpRequestInfo;
import http.HttpResponse;
import http.HttpStatus;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.QueryUtil;

import java.util.Map;
import java.util.UUID;

import static http.HttpMethod.POST;

public class UserLoginHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(UserLoginHandler.class);
    private static final int QUERY_SIZE = 2;

    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        logger.info("UserLoginHandler");
        if (request.getMethod() != POST) {
            throw new BaseException(HttpErrorCode.INVALID_HTTP_METHOD);
        }

        HttpResponse response = new HttpResponse();
        Map<String, String> queryParams = QueryUtil.parseQueryParams(request.getBody(), QUERY_SIZE);
        String userId = queryParams.get("userId");
        String password = queryParams.get("password");

        User user = Database.findUserById(userId);
        if (user == null || !user.getPassword().equals(password)) {
            logger.debug("User with id {} and password {} not found", userId, password);
            response.setStatus(HttpStatus.SEE_OTHER);
            response.setHeaders("Location", "/login/failed.html");
            return response;
        }

        String sid = UUID.randomUUID().toString();
        SessionManager.saveSession(sid, userId);

        response.setStatus(HttpStatus.FOUND);
        response.setCookies(createSessionCookie(sid));
        response.setHeaders("Location", "/index.html");

        return response;
    }

    private Cookie createSessionCookie(String sid) {
        Cookie cookie = new Cookie("sid", sid);
        cookie.setPath("/");
        return cookie;
    }
}
