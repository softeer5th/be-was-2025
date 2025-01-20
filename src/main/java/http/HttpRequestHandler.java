package http;

import handler.StaticResourceHandler;
import http.constant.HttpMethod;
import http.constant.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.PathPool;
import util.exception.NoSuchPathException;
import util.exception.NotAllowedMethodException;
import util.exception.SessionNotFoundException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HttpRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestHandler.class);

    public HttpRequestHandler() {}

    public void handleRequest(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        try {
            String path = httpRequest.getPath().toLowerCase();
            HttpMethod method = httpRequest.getMethod();

            if (PathPool.getInstance().isAvailable(method, path)) {
                Method classMethod = PathPool.getInstance().getMethod(method, path);
                classMethod.invoke(PathPool.getInstance().getClass(path), httpRequest, httpResponse);
                return;
            }

            StaticResourceHandler staticResourceHandler = new StaticResourceHandler(httpRequest, httpResponse);
            staticResourceHandler.handleStaticResource();
        } catch (NoSuchPathException e) {
            httpResponse.sendError(e.httpStatus, e.getMessage());
        } catch (NotAllowedMethodException e) {
            httpResponse.sendError(e.httpStatus, e.getMessage());
        } catch (IllegalAccessException e) {
            httpResponse.sendError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof SessionNotFoundException ex) {
                httpResponse.sendError(ex.httpStatus, e.getMessage());
            }
        }
    }
}
