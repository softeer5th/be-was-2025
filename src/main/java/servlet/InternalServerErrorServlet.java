package servlet;

import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;
import webserver.httpserver.StatusCode;

public class InternalServerErrorServlet implements Servlet {
    @Override
    public boolean handle(HttpRequest request, HttpResponse response) {
        response.setStatusCode(StatusCode.INTERNAL_SERVER_ERROR);
        response.setHeader("Content-Type", "text/html");
        response.setBody("<h1> 500 Internal Server Error </h1>".getBytes());
        return false;
    }
}
