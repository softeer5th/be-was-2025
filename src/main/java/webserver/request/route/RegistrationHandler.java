package webserver.request.route;

import model.ContentType;
import model.Cookie;
import webserver.*;
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

public class RegistrationHandler implements RequestProcessor {
    @Override
    public HTTPResponse handle(HTTPRequestHeader requestHeader, HTTPRequestBody requestBody, HTTPResponseHeader responseHeader, List<Cookie> cookieList) throws IOException {
        HTTPResponseBody responseBody;

        try {
            File file = new File("src/main/resources/static/registration/index.html");
            if (!file.exists()) {
                throw new HTTPExceptions.Error404("registration/index.html not found");
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
