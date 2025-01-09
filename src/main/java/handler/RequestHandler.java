package handler;

import http.HttpRequest;

public interface RequestHandler {
    void handle(HttpRequest httpRequest);
}
