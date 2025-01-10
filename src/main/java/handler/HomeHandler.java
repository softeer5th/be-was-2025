package handler;

import enums.FileContentType;
import enums.HttpMethod;
import enums.HttpStatus;
import exception.ClientErrorException;
import exception.ErrorCode;
import request.HttpRequestInfo;
import response.HttpResponse;

public class HomeHandler implements Handler {
    private static final String HOME_PATH = "http://localhost:8080/index.html";

    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        if (!request.getMethod().equals(HttpMethod.GET)) {
            throw new ClientErrorException(ErrorCode.INVALID_HTTP_METHOD);
        }

        HttpResponse response = new HttpResponse(HttpStatus.SEE_OTHER, FileContentType.HTML_UTF_8, "");
        response.setHeaders("Location", HOME_PATH);
        return response;
    }
}
