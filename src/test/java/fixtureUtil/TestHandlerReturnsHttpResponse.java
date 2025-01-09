package fixtureUtil;

import enums.FileContentType;
import enums.HttpStatus;
import handler.Handler;
import request.RequestInfo;
import response.HttpResponse;

public class TestHandlerReturnsHttpResponse implements Handler {
    @Override
    public HttpResponse handle(RequestInfo request) {
        return new HttpResponse(HttpStatus.OK, FileContentType.HTML_UTF_8, "test pass!");
    }
}
