package servlet;

import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;
import webserver.httpserver.StatusCode;

import java.io.IOException;

public class BadRequestServlet implements Servlet {
    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        response.setStatusCode(StatusCode.BAD_REQUEST);
        response.setHeader("Content-Type", "text/html");
        response.setBody("<h1> 400 Bad Request </h1>".getBytes());
    }
}
