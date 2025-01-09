package servlet;

import controller.HomeController;
import controller.SignUpController;
import exception.FileNotSupportedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServletManager {
    private static final String NOT_FOUND = "NOT_FOUND";
    private static final String FILE_NOT_SUPPORTED = "FILE_NOT_SUPPORTED";
    private static final String BAD_REQUEST = "BAD_REQUEST";
    private static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    private static final String DISPATCHER = "dispatcher";
    private static final String DEFAULT = "default";
    private static final Logger log = LoggerFactory.getLogger(ServletManager.class);
    private final Map<String, Servlet> servlets = new HashMap<>();
    public ServletManager(List<Object> controllers) {
        servlets.put("default", new StaticResourceServlet());
        servlets.put("dispatcher", new DispatcherServlet(controllers));
        servlets.put(FILE_NOT_SUPPORTED, new FileNotSupportedErrorPageServlet());
        servlets.put(NOT_FOUND, new FileNotFoundPageServlet());
        servlets.put(BAD_REQUEST, new BadRequestServlet());
        servlets.put(INTERNAL_SERVER_ERROR, new InternalServerErrorServlet());
    }

    public void addServlet(String url, Servlet servlet) {
        servlets.put(url, servlet);
    }

    public void serve(HttpRequest request, HttpResponse response) throws IOException {
        try {
            if (!servlets.get(DISPATCHER).handle(request, response)) {
                servlets.get(DEFAULT).handle(request, response);
            }
        } catch (FileNotSupportedException e) {
            servlets.get(FILE_NOT_SUPPORTED).handle(request, response);
        } catch (FileNotFoundException e){
            servlets.get(NOT_FOUND).handle(request, response);
        } catch (IOException | IllegalArgumentException e) {
            servlets.get(BAD_REQUEST).handle(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            servlets.get(INTERNAL_SERVER_ERROR).handle(request, response);
        }
    }
}
