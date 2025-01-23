package webserver.request.route;

import model.ContentType;
import model.Cookie;
import webserver.HTTPExceptions;
import webserver.request.HTTPRequestBody;
import webserver.request.HTTPRequestHeader;
import webserver.request.RequestProcessor;
import webserver.response.HTTPResponse;
import webserver.response.HTTPResponseBody;
import webserver.response.HTTPResponseHeader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class WritePageHandler implements RequestProcessor {
    @Override
    public HTTPResponse handle(HTTPRequestHeader requestHeader, HTTPRequestBody requestBody, String queryParams, HTTPResponseHeader responseHeader, List<Cookie> cookieList) throws IOException {
        boolean isLoggedIn = false;
        HTTPResponseBody responseBody = null;

        try {
            String method = requestHeader.getMethod();
            if (!method.equals("GET")) {
                throw new HTTPExceptions.Error405("Method not supported " + method);
            }

            for (Cookie cookie : cookieList) {
                if (cookie.getName().equals("SESSIONID")) {
                    isLoggedIn = true;
                    break;
                }
            }
            if (!isLoggedIn) {
                responseHeader.setStatusCode(302);
                responseHeader.addHeader("Location", "/login");
                return new HTTPResponse(responseHeader, responseBody);
            }

            File file = new File("src/main/resources/static/article/write.html");
            if (!file.exists()) {
                throw new HTTPExceptions.Error404("mypage/index.html not found");
            }

            responseBody = new HTTPResponseBody(Files.readAllBytes(file.toPath()));

            responseHeader.setStatusCode(200);
            responseHeader.addHeader("Content-Type", ContentType.getContentType(".html"));
            responseHeader.addHeader("Content-Length", Integer.toString(responseBody.getBodyLength()));
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
