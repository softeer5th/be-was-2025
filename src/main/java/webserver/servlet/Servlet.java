package webserver.servlet;

import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;

public interface Servlet {
    void handle(HttpRequest request, HttpResponse response);
}
