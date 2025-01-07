package webserver.servlet;

import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServletManager {
    private final Map<String, Servlet> servlets = new HashMap<>();

    public ServletManager() {
        servlets.put("default", new StaticResourceServlet());
        servlets.put("error", new ErrorPageServlet());
    }

    public void addServlet(String url, Servlet servlet) {
        servlets.put(url, servlet);
    }

    public void serve(HttpRequest request, HttpResponse response) throws IOException {
        String uri = request.getUri();
        try {
            if (!servlets.containsKey(uri)) {
                servlets.get("default").handle(request, response);
            }
            servlets.get(uri).handle(request, response);
        } catch (FileNotSupportedException e) {
            servlets.get("error").handle(request, response);
        }
    }
}
