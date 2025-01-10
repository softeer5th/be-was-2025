package handler;

import http.HttpRequest;
import http.HttpResponse;

public interface RequestHandler {
    boolean canHandle(HttpRequest httpRequest);
    HttpResponse handle(HttpRequest httpRequest);
}
