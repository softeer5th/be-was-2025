package webserver.exception.filter;

import webserver.enums.HttpStatusCode;
import webserver.exception.HttpException;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

public class HttpExceptionFilter implements ExceptionFilter {
    @Override
    public boolean canHandle(Exception e) {
        return e instanceof HttpException;
    }

    @Override
    public HttpResponse catchException(Exception e, HttpRequest request) {
        HttpException httpException = (HttpException) e;
        HttpResponse errorResponse = new HttpResponse(HttpStatusCode.of(httpException.getStatusCode()));
        errorResponse.setBody(e.getMessage());
        return errorResponse;
    }
}
