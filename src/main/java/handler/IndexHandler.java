package handler;


import webserver.handler.HttpHandler;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

public class IndexHandler implements HttpHandler {
    private static final String TEMPLATE_NAME = "/index.html";

    @Override
    public HttpResponse handleGet(HttpRequest request) {
        return HttpResponse.render(TEMPLATE_NAME);
    }
}
