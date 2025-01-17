package webserver.exception.resolver;

import webserver.enums.HttpStatusCode;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 여러 ExceptionResolver를 등록하고, 예외를 처리할 수 있는 Resolver를 찾아 처리해주는 클래스
public class ExceptionFilterChain {
    private final List<ExceptionFilter> resolvers = Collections.synchronizedList(new ArrayList<>());

    public ExceptionFilterChain addResolver(ExceptionFilter resolver) {
        resolvers.add(resolver);
        return this;
    }

    public HttpResponse catchException(Exception e, HttpRequest request) {
        for (ExceptionFilter resolver : resolvers) {
            if (resolver.canHandle(e)) {
                return resolver.catchException(e, request);
            }
        }
        return defaultResponse();
    }

    private HttpResponse defaultResponse() {
        return new HttpResponse(HttpStatusCode.INTERNAL_SERVER_ERROR);
    }
}
