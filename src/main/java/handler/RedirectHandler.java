package handler;

import enums.FileContentType;
import enums.HttpHeader;
import enums.HttpStatus;
import request.HttpRequestInfo;
import response.HttpResponse;

public class RedirectHandler implements Handler {
    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        HttpResponse response = new HttpResponse();
        response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8);
        response.setHeader(HttpHeader.LOCATION.getName(),request.getPath()+"/index.html");
        return response;
    }
}
