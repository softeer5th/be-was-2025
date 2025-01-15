package http.handler;

import http.enums.ErrorMessage;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.enums.HttpResponseStatus;

import java.io.IOException;

public class BadRequestHandler implements Handler {
    private static BadRequestHandler instance = new BadRequestHandler();

    private BadRequestHandler() {}

    public static BadRequestHandler getInstance() {
        return instance;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        response.sendErrorResponse(HttpResponseStatus.BAD_REQUEST, ErrorMessage.BAD_REQUEST);
    }
}

