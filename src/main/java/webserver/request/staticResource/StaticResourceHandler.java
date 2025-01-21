package webserver.request.staticResource;

import webserver.*;
import webserver.request.RequestProcessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class StaticResourceHandler implements RequestProcessor {
    @Override
    public HTTPResponse handle(HTTPRequestHeader requestHeader, HTTPRequestBody requestBody, HTTPResponseHeader responseHeader, List<Cookie> cookieList) throws IOException {
        HTTPResponseBody responseBody;

        try {
            String[] uri = requestHeader.getUri().split("\\?");
            String path = uri[0];
            File file = new File("src/main/resources/static/" + path);

            if (!file.exists()) {
                throw new HTTPExceptions.Error404("404 Not Found");
            }
            responseBody = new HTTPResponseBody(Files.readAllBytes(file.toPath()));

            responseHeader.setStatusCode(200);
            responseHeader.addHeader("Content-Type", ContentTypeMapper.getContentType(path));
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
