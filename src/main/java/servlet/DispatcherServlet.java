package servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wasframework.ControllerAdaptor;
import wasframework.ControllerMapping;
import wasframework.ControllerMethod;
import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class DispatcherServlet implements Servlet {
    private static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);
    private final List<Object> controllers;
    private final ControllerMapping controllerMapping;
    private final ControllerAdaptor controllerAdaptor = new ControllerAdaptor();

    public DispatcherServlet(List<Object> controllers) {
        this.controllers = controllers;
        this.controllerMapping = new ControllerMapping(controllers);
    }

    @Override
    public boolean handle(HttpRequest request, HttpResponse response) throws IOException {
        ControllerMethod controllerMethod = controllerMapping
                .getControllerMethod(request.getUri(), request.getMethod());
        if (controllerMethod == null) {
            return false;
        }
        try {
            controllerAdaptor.invoke(controllerMethod,request, response);
        } catch (InvocationTargetException e) {
            log.error(e.getTargetException().getMessage(), e.getTargetException());
            return false;
        }
        return true;
    }
}
