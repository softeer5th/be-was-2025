package webserver.handler;

import webserver.enums.HttpStatusCode;
import webserver.exception.NotImplemented;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;


// HTTP 요청을 처리하는 핸들러 인터페이스
public interface HttpHandler {

    // HTTP 요청을 처리하고 응답을 반환.
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

    // 필요에 맞게 handleXXX 메서드를 구현하여 사용
    // 구현하지 않은 메서드에 대해선 NotFound 응답
    default HttpResponse handleGet(HttpRequest request) {
        return returnNotFound();
    }

    default HttpResponse handlePost(HttpRequest request) {
        return returnNotFound();
    }

    default HttpResponse handlePut(HttpRequest request) {
        return returnNotFound();
    }

    default HttpResponse handleDelete(HttpRequest request) {
        return returnNotFound();
    }

    default HttpResponse handlePatch(HttpRequest request) {
        return returnNotFound();
    }


    default HttpResponse handleHead(HttpRequest request) {
        return returnNotFound();
    }

    private HttpResponse returnNotFound() {
        return new HttpResponse(HttpStatusCode.NOT_FOUND);
    }
}
