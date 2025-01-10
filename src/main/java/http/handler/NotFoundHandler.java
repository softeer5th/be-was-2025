package http.handler;

import http.request.HttpRequest;
import http.response.HttpResponse;
import http.enums.HttpResponseStatus;

import java.io.IOException;

public class NotFoundHandler implements Handler {
    private static NotFoundHandler instance = new NotFoundHandler();

    private NotFoundHandler() {}

    public static NotFoundHandler getInstance() {
        return instance;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        response.sendErrorResponse(HttpResponseStatus.NOT_FOUND);
    }
}

