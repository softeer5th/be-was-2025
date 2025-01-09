package webserver.http.servlet;

import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.HttpStatus;

public abstract class HttpServlet {

    protected void service(HttpRequest request, HttpResponse response) {
        String method = request.getMethod();
        switch (method.toUpperCase()) {
            case "GET":
                doGet(request, response);
                break;
            case "POST":
                doPost(request, response);
                break;
            default:
                response.setStatus(HttpStatus.NOT_FOUND);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.NOT_FOUND);
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.NOT_FOUND);
    }
}
