package webserver.exception.filter;

import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

/**
 * 예외 발생 시 이를 처리하여 응답을 생성하는 인터페이스
 */
public interface ExceptionFilter {

    /**
     * 예외를 처리할 수 있는지 여부를 반환한다.
     * Chain of Responsibility 패턴을 적용하기 위해 사용된다.
     *
     * @param e 예외
     * @return 예외를 처리할 수 있는지 여부
     */
    boolean canHandle(Exception e);

    /**
     * 예외를 처리하여 응답을 생성한다.
     *
     * @param e       예외
     * @param request 요청
     * @return 응답
     */
    HttpResponse catchException(Exception e, HttpRequest request);
}
