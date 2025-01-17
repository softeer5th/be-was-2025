package http.handler;

import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;

public interface Handler {
    HttpResponse handle(HttpRequest request) throws IOException;
}
