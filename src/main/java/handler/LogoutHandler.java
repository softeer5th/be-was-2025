package handler;

import webserver.handler.HttpHandler;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

import static enums.PageMappingPath.INDEX;

/**
 * 로그아웃 요청을 처리하는 핸들러
 */
public class LogoutHandler implements HttpHandler {
    /**
     * 로그아웃 처리
     */
    @Override
    public HttpResponse handlePost(HttpRequest request) {
        request.getSession().invalidate();
        return HttpResponse.redirect(INDEX.path);
    }
}
