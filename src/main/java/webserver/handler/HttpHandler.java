package webserver.handler;

import webserver.enums.HttpStatusCode;
import webserver.exception.HttpException;
import webserver.exception.NotImplemented;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

/**
 * HTTP 요청을 처리하는 핸들러 인터페이스
 */
public interface HttpHandler {

    /**
     * HTTP 요청을 처리하는 메서드
     *
     * @param request HTTP 요청
     * @return HTTP 응답
     */
    default HttpResponse handle(HttpRequest request) {
        return switch (request.getMethod()) {
            case GET -> handleGet(request);
            case HEAD -> handleHead(request);
            case POST -> handlePost(request);
            case PUT -> handlePut(request);
            case PATCH -> handlePatch(request);
            case DELETE -> handleDelete(request);
            default -> throw new NotImplemented("Not implemented method: " + request.getMethod());
        };
    }

    default HttpResponse handleGet(HttpRequest request) {
        return throwException();
    }

    default HttpResponse handlePost(HttpRequest request) {
        return throwException();
    }

    default HttpResponse handlePut(HttpRequest request) {
        return throwException();
    }

    default HttpResponse handleDelete(HttpRequest request) {
        return throwException();
    }

    default HttpResponse handlePatch(HttpRequest request) {
        return throwException();
    }


    default HttpResponse handleHead(HttpRequest request) {
        return throwException();
    }

    private HttpResponse throwException() {
        throw new HttpException(HttpStatusCode.METHOD_NOT_ALLOWED, "Method Not Allowed");
    }
}
