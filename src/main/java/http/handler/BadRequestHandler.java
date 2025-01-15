package http.handler;

import http.enums.ErrorMessage;
import http.enums.HttpResponseStatus;
import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;

public class BadRequestHandler implements Handler {
    private static final BadRequestHandler instance = new BadRequestHandler();

    private BadRequestHandler() {}

    public static BadRequestHandler getInstance() {
        return instance;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        return new HttpResponse.Builder()
                .errorResponse(HttpResponseStatus.BAD_REQUEST, ErrorMessage.BAD_REQUEST)
                .build();
    }
}

