package handler;

import http.request.HttpRequest;
import http.response.HttpResponse;

public interface RequestHandler {
    boolean canHandle(HttpRequest httpRequest);
    HttpResponse handle(HttpRequest httpRequest);
}
