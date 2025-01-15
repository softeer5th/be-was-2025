package webserver.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public Context preHandle(HttpRequest request) {
        log.debug("Request: {}", request);
        return Context.empty();
    }

    @Override
    public HttpResponse postHandle(HttpRequest request, HttpResponse response, Context unused) {
        log.debug("Response: {}", response);
        return response;
    }
}
