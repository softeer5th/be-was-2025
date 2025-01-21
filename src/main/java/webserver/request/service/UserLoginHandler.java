package webserver.request.service;

import db.Database;
import model.Cookie;
import model.Session;
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

public class UserLoginHandler implements RequestProcessor {
    static final int MAX_LOGIN_SESSION_TIME = 3600;

    @Override
    public HTTPResponse handle(HTTPRequestHeader requestHeader, HTTPRequestBody requestBody, HTTPResponseHeader responseHeader, List<Cookie> cookieList) throws IOException {
        HTTPResponseBody responseBody = null;

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
            if (paramMap.size() != 2) {
                throw new HTTPExceptions.Error400("Wrong number of parameters");
            }

            String userId = paramMap.get("id");
            String userPassword = paramMap.get("password");

            if (userId == null || userId.isEmpty() || userPassword == null || userPassword.isEmpty()) {
                throw new HTTPExceptions.Error400("Missing required parameters");
            }

            User user;
            if ((user = Database.findUserById(userId)) != null) {
                if (user.getPassword().equals(userPassword)) {
                    Session session = new Session(userId, MAX_LOGIN_SESSION_TIME);
                    cookieList.add(new Cookie("SESSIONID", session.getSessionId(), session.getMaxInactiveInterval()));

                    Database.addSession(session);

                    responseHeader.setStatusCode(302);
                    responseHeader.addHeader("Location", "/index.html");
                }
                else {
                    //logger.error("User {} password does not match", userId);
                    responseHeader.setStatusCode(302);
                    responseHeader.addHeader("Location", "/user/login_failed.html");
                }
            }
            else {
                //logger.error("User {} not found", userId);
                responseHeader.setStatusCode(302);
                responseHeader.addHeader("Location", "/user/login_failed.html");
            }
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
