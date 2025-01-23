package webserver.exception.filter;

import webserver.enums.ContentType;
import webserver.enums.HttpStatusCode;
import webserver.exception.HttpException;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

/**
 * HttpException을 처리하는 ExceptionFilter
 */
public class HttpExceptionFilter implements ExceptionFilter {
    private static final String ERROR_HTML = "<html><head><title>Error</title></head><body>%s</body></html>";

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

        HttpStatusCode statusCode = HttpStatusCode.of(httpException.getStatusCode());

        String errorMessage = String.format(ERROR_HTML, statusCode.statusCode + " " + statusCode.reasonPhrase);
        errorResponse.setBody(errorMessage.getBytes(), ContentType.TEXT_HTML);
        return errorResponse;
    }

}
