package webserver.exception.resolver;

import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

// 예외 발생 시 이를 처리하여 응답을 생성하는 인터페이스
public interface ExceptionFilter {

    boolean canHandle(Exception e);

    HttpResponse catchException(Exception e, HttpRequest request);
}
