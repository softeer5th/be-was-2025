package webserver.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

/**
 * Request, Response 관련 로그를 기록하는 클래스
 */
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public HttpRequest preHandle(HttpRequest request, Context unused) {
        log.debug("Request: {}", request);
        return request;
    }

    @Override
    public HttpResponse postHandle(HttpRequest request, HttpResponse response, Context unused) {
        log.debug("Response: {}", response);
        return response;
    }
}
