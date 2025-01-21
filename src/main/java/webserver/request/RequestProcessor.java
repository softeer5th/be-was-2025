package webserver.request;

import model.Cookie;
import webserver.response.HTTPResponse;
import webserver.response.HTTPResponseHeader;

import java.io.IOException;
import java.util.List;

public interface RequestProcessor {
    HTTPResponse handle(HTTPRequestHeader requestHeader, HTTPRequestBody requestBody, HTTPResponseHeader responseHeader, List<Cookie> cookieList) throws IOException;
}
