package servlet;

import exception.FileNotSupportedException;
import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServletManager {
    private static final String NOT_FOUND = "NOT_FOUND";
    private static final String FILE_NOT_SUPPORTED = "FILE_NOT_SUPPORTED";
    private static final String BAD_REQUEST = "BAD_REQUEST";
    private final Map<String, Servlet> servlets = new HashMap<>();
    public ServletManager() {
        servlets.put("default", new StaticResourceServlet());
        servlets.put(FILE_NOT_SUPPORTED, new FileNotSupportedErrorPageServlet());
        servlets.put(NOT_FOUND, new FileNotFoundPageServlet());
        servlets.put(BAD_REQUEST, new BadRequestServlet());
    }

    public void addServlet(String url, Servlet servlet) {
        servlets.put(url, servlet);
    }

    public void serve(HttpRequest request, HttpResponse response) throws IOException {
        String uri = request.getUri();
        try {
            if (!servlets.containsKey(uri)) {
                servlets.get("default").handle(request, response);
                return;
            }
            servlets.get(uri).handle(request, response);
        } catch (FileNotSupportedException e) {
            servlets.get(FILE_NOT_SUPPORTED).handle(request, response);
        } catch (FileNotFoundException e){
            servlets.get(NOT_FOUND).handle(request, response);
        } catch (IOException | IllegalArgumentException e) {
            servlets.get(BAD_REQUEST).handle(request, response);
        }
    }
}
