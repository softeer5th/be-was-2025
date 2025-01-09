package api;

import http.HttpRequest;

public interface Controller {
    void processGet(HttpRequest httpRequest);
}
