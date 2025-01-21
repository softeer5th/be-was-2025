package webserver.request;

import webserver.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface RequestProcessor {
    HTTPResponse handle(HTTPRequestHeader requestHeader, HTTPRequestBody requestBody, HTTPResponseHeader responseHeader, List<Cookie> cookieList) throws IOException;
}
