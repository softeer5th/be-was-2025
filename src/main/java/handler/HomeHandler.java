package handler;

import enums.FileContentType;
import enums.HttpStatus;
import exception.ClientErrorException;
import exception.ErrorCode;
import request.HttpRequestInfo;
import response.HttpResponse;

import static enums.HttpMethod.GET;

public class HomeHandler implements Handler {
    private static final String HOME_URL = System.getenv("HOME_URL");

    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        checkHttpMethod(request);
        HttpResponse response = new HttpResponse(HttpStatus.SEE_OTHER, FileContentType.HTML_UTF_8, "");
        response.setHeaders("Location", HOME_URL);
        return response;
    }

    private static void checkHttpMethod(HttpRequestInfo request) {
        if (!request.getMethod().equals(GET)) {
            throw new ClientErrorException(ErrorCode.METHOD_NOT_ALLOWED);
        }
    }
}
