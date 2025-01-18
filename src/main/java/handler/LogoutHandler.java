package handler;

import util.enums.CookieName;
import util.enums.HttpStatusCode;
import util.enums.Page;
import webserver.cookie.CookieManager;
import webserver.request.Request;
import webserver.response.Response;
import webserver.session.SessionManager;

public class LogoutHandler extends Handler{
    @Override
    public Response handle(Request request){
        Response response = new Response(request, HttpStatusCode.FOUND);
        if(sessionId == null){
            response.setStatusCode(HttpStatusCode.SEE_OTHER);
            response.addHeader("Location", Page.LOGIN.getPath());
            return response;
        }
        SessionManager.removeSession(sessionId);
        String deleteCookieString = CookieManager.deleteCookie(CookieName.SESSION_COOKIE);
        response.addHeader("Location", Page.MAIN_PAGE.getPath());
        response.addHeader("Set-Cookie", deleteCookieString);
        return response;
    }
}
