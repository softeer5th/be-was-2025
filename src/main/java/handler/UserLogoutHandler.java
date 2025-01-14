package handler;

import db.SessionManager;
import exception.BaseException;
import exception.HttpErrorCode;
import http.HttpRequestInfo;
import http.HttpResponse;
import http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static http.HttpMethod.POST;

public class UserLogoutHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(UserLogoutHandler.class);


    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        if (request.getMethod() != POST) {
            throw new BaseException(HttpErrorCode.INVALID_HTTP_METHOD);
        }
        HttpResponse response = new HttpResponse();
        Map<String, String> params = parseQueryParams(request.getBody());
        SessionManager.removeSession(params.get("sid"));

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
        if (pairs.length != 1) {
            logger.error("Query pair size is not 1");
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
