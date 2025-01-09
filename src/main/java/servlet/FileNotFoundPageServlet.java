package servlet;

import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;
import webserver.httpserver.StatusCode;

import java.io.IOException;

public class FileNotFoundPageServlet implements Servlet {
    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        response.setStatusCode(StatusCode.NOT_FOUND);
        response.setHeader("Content-Type", "text/html");
        response.setBody("<h1> 404 Not Found </h1>".getBytes());
    }
}
