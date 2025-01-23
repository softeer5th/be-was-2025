package webserver.exception.filter;

import webserver.exception.LoginRequired;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

/**
 * 로그인 없이 접근할 경우 발생하는 예외를 처리하는 필터
 */
public class LoginRequiredFilter implements ExceptionFilter {
    /**
     * 예외가 LoginRequired 인스턴스인지 확인
     *
     * @param e 예외
     * @return LoginRequired 인스턴스인지 여부
     */
    @Override
    public boolean canHandle(Exception e) {
        return e instanceof LoginRequired;
    }

    /**
     * 로그인 페이지로 리다이렉트
     *
     * @param e       예외
     * @param request 요청
     * @return 로그인 페이지로 리다이렉트하는 응답
     */
    @Override
    public HttpResponse catchException(Exception e, HttpRequest request) {
        return HttpResponse.redirect(((LoginRequired) e).getRedirectLocation());
    }
}
