package http.handler;

import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;

public interface Handler {
    void handle(HttpRequest request, HttpResponse response) throws IOException;
}
