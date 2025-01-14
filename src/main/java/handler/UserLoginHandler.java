package handler;

import db.Database;
import db.SessionManager;
import exception.BaseException;
import exception.HttpErrorCode;
import http.HttpRequestInfo;
import http.HttpResponse;
import http.HttpStatus;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.SessionUtil;

import java.util.HashMap;
import java.util.Map;

import static http.HttpMethod.POST;

public class UserLoginHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(UserLoginHandler.class);


    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        logger.info("UserLoginHandler");
        if (request.getMethod() != POST) {
            throw new BaseException(HttpErrorCode.INVALID_HTTP_METHOD);
        }
        HttpResponse response = new HttpResponse();
        Map<String, String> queryParams = parseQueryParams(request.getBody());
        String userId = queryParams.get("userId");
        String password = queryParams.get("password");

        User user = Database.findUserById(userId);
        if (user == null || !user.getPassword().equals(password)) {
            logger.debug("User with id {} and password {} not found", userId, password);
            response.setStatus(HttpStatus.FOUND);
            response.setHeaders("Location", "/login/failed.html");
            return response;
        }

        String sid = SessionUtil.generateSessionID();
        SessionManager.saveSession(sid, userId);

        response.setCookies("sid=" + sid);
        response.setCookies("Path=/");
        response.setStatus(HttpStatus.FOUND);
        response.setHeaders("Location", "/index.html");

        return response;
    }

    private Map<String, String> parseQueryParams(String query) throws BaseException {
        Map<String, String> params = new HashMap<>();
        if (query.isEmpty()) {
            logger.error("Query string is empty");
            throw new BaseException(HttpErrorCode.INVALID_QUERY_PARAM);
        }
        String[] pairs = query.split("&");
        if (pairs.length != 2) {
            logger.error("Query pair size is not 2");
            throw new BaseException(HttpErrorCode.INVALID_QUERY_PARAM);
        }
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            } else {
                logger.error("Query string is not pair");
                throw new BaseException(HttpErrorCode.INVALID_QUERY_PARAM);
            }
        }
        return params;
    }
}
