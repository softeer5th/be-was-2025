package servlet;

import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;
import webserver.httpserver.StatusCode;

public class FileNotSupportedErrorPageServlet implements Servlet {
    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        response.setStatusCode(StatusCode.NOT_SUPPORTED);
        response.setHeader("Content-Type", "text/html");
        response.setBody("<h1> 406 Not Supported </h1>".getBytes());
    }
}
