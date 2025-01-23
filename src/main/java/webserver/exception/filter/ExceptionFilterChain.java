package webserver.exception.filter;

import webserver.enums.HttpStatusCode;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 여러 ExceptionFilter를 등록하고, 예외를 처리할 수 있는 Filter를 찾아 처리해주는 클래스
 */
public class ExceptionFilterChain {
    private final List<ExceptionFilter> filters = Collections.synchronizedList(new ArrayList<>());

    /**
     * ExceptionFilter를 추가한다.
     *
     * @param filter 추가할 ExceptionFilter
     * @return this
     */
    public ExceptionFilterChain addFilter(ExceptionFilter filter) {
        filters.add(filter);
        return this;
    }

    /**
     * exception을 처리할 수 있는 filter를 찾아서 처리한다.
     * chain of responsibility pattern
     *
     * @param e       발생한 예외
     * @param request Exception Filter 가 예외를 처리할 때 필요한 request
     * @return 예외를 처리한 response. 없다면 500 Internal Server Error
     */
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
