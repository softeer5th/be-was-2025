package webserver.exception.filter;

import webserver.enums.HttpStatusCode;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 여러 ExceptionFilter를 등록하고, 예외를 처리할 수 있는 Resolver를 찾아 처리해주는 클래스
public class ExceptionFilterChain {
    private final List<ExceptionFilter> filters = Collections.synchronizedList(new ArrayList<>());

    public ExceptionFilterChain addFilter(ExceptionFilter filter) {
        filters.add(filter);
        return this;
    }

    public HttpResponse catchException(Exception e, HttpRequest request) {
        for (ExceptionFilter filter : filters) {
            if (filter.canHandle(e)) {
                return filter.catchException(e, request);
            }
        }
        return defaultResponse();
    }

    private HttpResponse defaultResponse() {
        return new HttpResponse(HttpStatusCode.INTERNAL_SERVER_ERROR);
    }
}
