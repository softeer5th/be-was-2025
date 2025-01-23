package webserver.exception.filter;

import webserver.enums.HttpStatusCode;
import webserver.exception.HttpException;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

/**
 * HttpException을 처리하는 ExceptionFilter
 */
public class HttpExceptionFilter implements ExceptionFilter {
    /**
     * HttpException을 처리할 수 있는지 여부를 반환한다.
     *
     * @param e Exception
     * @return HttpException일 경우 true, 아닐 경우 false
     */
    @Override
    public boolean canHandle(Exception e) {
        return e instanceof HttpException;
    }

    /**
     * HttpException을 처리한다.
     *
     * @param e       Exception
     * @param request HttpRequest
     * @return 응답코드로 HttpException의 statusCode를 담은 HttpResponse
     */
    @Override
    public HttpResponse catchException(Exception e, HttpRequest request) {
        HttpException httpException = (HttpException) e;
        HttpResponse errorResponse = new HttpResponse(HttpStatusCode.of(httpException.getStatusCode()));
        errorResponse.setBody(e.getMessage());
        return errorResponse;
    }
}
