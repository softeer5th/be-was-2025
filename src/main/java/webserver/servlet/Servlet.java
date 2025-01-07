package webserver.servlet;

import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;

import java.io.IOException;

public interface Servlet {
    void handle(HttpRequest request, HttpResponse response) throws IOException;
}
