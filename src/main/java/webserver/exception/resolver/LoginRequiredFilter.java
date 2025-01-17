package webserver.exception.resolver;

import webserver.exception.LoginRequired;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

public class LoginRequiredFilter implements ExceptionFilter {
    @Override
    public boolean canHandle(Exception e) {
        return e instanceof LoginRequired;
    }

    @Override
    public HttpResponse catchException(Exception e, HttpRequest request) {
        return HttpResponse.redirect(((LoginRequired) e).getRedirectLocation());
    }
}
