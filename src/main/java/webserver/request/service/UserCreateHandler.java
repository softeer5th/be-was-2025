package webserver.request.service;

import db.Database;
import model.Cookie;
import model.User;
import webserver.*;
import webserver.request.HTTPRequestBody;
import webserver.request.HTTPRequestHeader;
import webserver.request.RequestProcessor;
import webserver.response.HTTPResponse;
import webserver.response.HTTPResponseBody;
import webserver.response.HTTPResponseHeader;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserCreateHandler implements RequestProcessor {
    @Override
    public HTTPResponse handle(HTTPRequestHeader requestHeader, HTTPRequestBody requestBody, String queryParams, HTTPResponseHeader responseHeader, List<Cookie> cookieList) throws IOException {
        HTTPResponseBody responseBody;

        try {
            String method = requestHeader.getMethod();
            if (!method.equals("POST")) {
                throw new HTTPExceptions.Error405("Method not supported " + method);
            }

            Map<String, String> headers = requestHeader.getHeaders();
            String contentType = headers.get("content-type");
            if (!contentType.equals("application/x-www-form-urlencoded")) {
                throw new HTTPExceptions.Error415("Unsupported Media Type " + contentType);
            }

            String[] params = requestBody.getBodyToString().split("&");
            Map<String, String> paramMap = new HashMap<>();
            for (String param : params) {
                String[] keyValue = param.split("=");
                // 키값에 등호가 있을 경우
                if (keyValue.length != 2) {
                    throw new HTTPExceptions.Error400("Unsupported parameter: " + param);
                }
                // 키값 중복
                if (paramMap.containsKey(keyValue[0])) {
                    throw new HTTPExceptions.Error400("Duplicate key");
                }
                paramMap.put(keyValue[0], URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
            }
            // 잘못된 키값 입력
            if (paramMap.size() != 3) {
                throw new HTTPExceptions.Error400("wrong number of parameters");
            }

            String userId = paramMap.get("id");
            String userName = paramMap.get("name");
            String userPassword = paramMap.get("password");

            // 중복된 id를 가진 사용자가 있을 경우
            if (Database.getUserById(userId) != null) {
                // 409 Conflict
                throw new HTTPExceptions.Error409("User already exists");
            }

            // User 데이터베이스에 사용자 정보 추가
            User user = new User(userId, userPassword, userName);
            Database.addUser(user);

            responseBody = null;

            responseHeader.setStatusCode(302);
            responseHeader.addHeader("Location", "/index.html");
        } catch (HTTPExceptions e) {
            responseHeader.setStatusCode(e.getStatusCode());
            responseBody = new HTTPResponseBody(HTTPExceptions.getErrorMessageToBytes(e.getMessage()));
        }

        for (Cookie cookie : cookieList) {
            responseHeader.addHeader("Set-Cookie", cookie.toString());
        }

        return new HTTPResponse(responseHeader, responseBody);
    }
}
