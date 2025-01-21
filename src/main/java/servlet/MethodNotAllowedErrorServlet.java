package servlet;

import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;
import webserver.httpserver.StatusCode;

public class MethodNotAllowedErrorServlet implements Servlet{
    public boolean handle(HttpRequest request, HttpResponse response) {
        response.setStatusCode(StatusCode.METHOD_NOT_ALLOWED);
        response.setHeader("Content-Type", "text/html");
        response.setBody("<h1> 405 Method Not Allowed </h1>".getBytes());
        return false;
    }
}
