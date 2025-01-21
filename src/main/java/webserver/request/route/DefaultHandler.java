package webserver.request.route;

import webserver.*;
import webserver.request.RequestProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class DefaultHandler implements RequestProcessor {
    @Override
    public HTTPResponse handle(HTTPRequestHeader requestHeader, HTTPRequestBody requestBody, HTTPResponseHeader responseHeader, List<Cookie> cookieList) throws IOException {
        responseHeader.setStatusCode(302);
        responseHeader.addHeader("Location", "/index.html");

        for (Cookie cookie : cookieList) {
            responseHeader.addHeader("Set-Cookie", cookie.toString());
        }

        return new HTTPResponse(responseHeader, null);
    }
}
