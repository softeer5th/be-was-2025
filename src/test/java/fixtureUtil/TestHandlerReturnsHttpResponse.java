package fixtureUtil;

import enums.FileContentType;
import enums.HttpStatus;
import handler.Handler;
import request.HttpRequestInfo;
import response.HttpResponse;

public class TestHandlerReturnsHttpResponse implements Handler {
    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        return new HttpResponse(HttpStatus.OK, FileContentType.HTML_UTF_8, "test pass!");
    }
}
