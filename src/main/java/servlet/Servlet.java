package servlet;

import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;

import java.io.IOException;

public interface Servlet {
    /**
     * 서블릿 인터페이스
     * @param request
     * @param response
     * @return 서빙 성공시 true, 실패 시 false 반환
     * @throws IOException
     */
    boolean handle(HttpRequest request, HttpResponse response) throws IOException;
}
