package webserver.request.service;

import db.Database;
import model.Article;
import model.Cookie;
import webserver.HTTPExceptions;
import webserver.request.HTTPRequestBody;
import webserver.request.HTTPRequestHeader;
import webserver.request.RequestProcessor;
import webserver.response.HTTPResponse;
import webserver.response.HTTPResponseBody;
import webserver.response.HTTPResponseHeader;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserWriteHandler implements RequestProcessor {

    @Override
    public HTTPResponse handle(HTTPRequestHeader requestHeader, HTTPRequestBody requestBody, String queryParams, HTTPResponseHeader responseHeader, List<Cookie> cookieList) throws IOException {
        boolean isLoggedIn = false;
        HTTPResponseBody responseBody = null;

        try {
            String method = requestHeader.getMethod();
            if (!method.equals("POST")) {
                throw new HTTPExceptions.Error405("Method not supported " + method);
            }

            // 쿠키 정보를 바탕으로 사용자 정보 가져오기
            String userId = null, userName = null;
            for (Cookie cookie : cookieList) {
                if (cookie.getName().equals("SESSIONID")) {
                    isLoggedIn = true;
                    String sessionId = cookie.getValue();
                    userId = Database.getSessionById(sessionId).getUserId();
                    userName = Database.getUserById(userId).getName();
                    break;
                }
            }
            if (!isLoggedIn) {
                responseHeader.setStatusCode(302);
                responseHeader.addHeader("Location", "/login");
                return new HTTPResponse(responseHeader, responseBody);
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
            if (paramMap.size() != 1) {
                throw new HTTPExceptions.Error400("Wrong number of parameters");
            }

            String content = paramMap.get("content");

            if (content == null || content.isEmpty()) {
                throw new HTTPExceptions.Error400("Missing required parameters");
            }

            Article article = new Article(content, userId, userName, LocalTime.now());
            Database.addArticle(article);

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
