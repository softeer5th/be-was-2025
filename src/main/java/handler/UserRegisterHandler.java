package handler;

import static http.HttpMethod.GET;

import exception.BaseException;
import exception.HttpErrorCode;
import http.HttpRequestInfo;
import http.HttpStatus;
import java.util.HashMap;
import java.util.Map;
import model.User;
import http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

public class UserRegisterHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private static final String USER_REQUEST_PREFIX = "/users/";

    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        String path = request.getPath().substring(USER_REQUEST_PREFIX.length());
        HttpResponse response = new HttpResponse();

        if (request.getMethod().equals(GET) && path.startsWith("register?")) {
            String query = path.substring("register?".length());
            Map<String, String> queryParams = parseQueryParams(query);

            String userId = queryParams.get("userId");
            String nickname = queryParams.get("nickname");
            String password = queryParams.get("password");
            String email = queryParams.get("email");

            User user = new User(userId, nickname, password, email);
            user.registerUser();

            response.setStatus(HttpStatus.FOUND);
            response.setHeaders("Location", "/registration/success.html");
        }

        return response;
    }

    private Map<String, String> parseQueryParams(String query) throws BaseException {
        Map<String, String> params = new HashMap<>();
        if (query.isEmpty()) {
            logger.error("Query string is empty");
            throw new BaseException(HttpErrorCode.INVALID_QUERY_PARAM);
        }
        String[] pairs = query.split("&");
        if (pairs.length != 4) {
            logger.error("Query pair size is not 4");
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