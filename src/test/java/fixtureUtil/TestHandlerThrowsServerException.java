package fixtureUtil;


import exception.ClientErrorException;
import exception.ErrorCode;
import handler.Handler;
import request.HttpRequestInfo;
import response.HttpResponse;

public class TestHandlerThrowsServerException implements Handler {
    @Override
    public HttpResponse handle(HttpRequestInfo request) {
       throw new ClientErrorException(ErrorCode.ERROR_WITH_ENCODING);
    }

}

