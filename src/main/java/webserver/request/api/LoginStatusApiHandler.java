package webserver.request.api;

import db.Database;
import webserver.*;
import webserver.request.RequestProcessor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class LoginStatusApiHandler implements RequestProcessor {

    @Override
    public HTTPResponse handle(HTTPRequestHeader requestHeader, HTTPRequestBody requestBody, HTTPResponseHeader responseHeader, List<Cookie> cookieList) throws IOException {
        HTTPResponseBody responseBody;
        boolean isLoggedIn = false;
        String userName = "";

        for (Cookie cookie : cookieList) {
            if (cookie.getName().equals("SESSIONID")) {
                isLoggedIn = true;
                String sessionId = cookie.getValue();
                String userId = Database.findSessionById(sessionId).getUserId();
                userName = Database.findUserById(userId).getName();
                break;
            }
        }

        String jsonResponse = "{\"isLoggedIn\": " + isLoggedIn + ", \"userName\": \"" + userName +  "\"}";
        responseBody = new HTTPResponseBody(jsonResponse.getBytes(StandardCharsets.UTF_8));

        responseHeader.setStatusCode(200);
        responseHeader.addHeader("Content-Type", ContentTypeMapper.getContentType(".json"));
        for (Cookie cookie : cookieList) {
            responseHeader.addHeader("Set-Cookie", cookie.toString());
        }

        return new HTTPResponse(responseHeader, responseBody);
    }
}
