package webserver.handler;

import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

import static webserver.enums.PageMappingPath.INDEX;

// 로그아웃을 담당하는 핸들러
public class LogoutHandler implements HttpHandler {
    @Override
    public HttpResponse handlePost(HttpRequest request) {
        request.getSession().invalidate();
        return HttpResponse.redirect(INDEX.path);
    }
}
