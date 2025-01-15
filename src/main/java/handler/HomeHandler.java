package handler;

import exception.ClientErrorException;
import request.HttpRequestInfo;
import response.HttpResponse;

import static enums.FileContentType.HTML_UTF_8;
import static enums.HttpMethod.GET;
import static enums.HttpStatus.SEE_OTHER;
import static exception.ErrorCode.REQUEST_NOT_ALLOWED;

public class HomeHandler implements Handler {
    private static final String HOME_URL = System.getenv("HOME_URL");

    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        checkHttpMethod(request);
        HttpResponse response = new HttpResponse(SEE_OTHER, HTML_UTF_8, "");
        response.setHeaders("Location", HOME_URL);
        return response;
    }

    private static void checkHttpMethod(HttpRequestInfo request) {
        if (!request.getMethod().equals(GET)) {
            throw new ClientErrorException(REQUEST_NOT_ALLOWED);
        }
    }
}
