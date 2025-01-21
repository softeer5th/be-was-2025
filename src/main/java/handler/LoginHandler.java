package handler;

import model.User;
import util.Parameter;
import util.PostManager;
import util.UserManager;
import util.enums.CookieName;
import util.enums.HttpStatusCode;
import util.enums.Page;
import webserver.cookie.CookieManager;
import webserver.request.Request;
import webserver.response.Response;
import webserver.session.Session;
import webserver.session.SessionManager;

public class LoginHandler extends Handler{
    @Override
    public Response handle(Request request) {
        Response response = new Response(request, HttpStatusCode.FOUND);
        try {
            User user = UserManager.logIn(request.getBody());
            Session session = SessionManager.createSession(user);
            String setCookieString = new CookieManager
                    .SetCookie(CookieName.SESSION_COOKIE.getName(), session.getId())
                    .path(Page.MAIN_PAGE.getPath()).build();
            String path = Parameter
                    .setPostId(Page.MAIN_LOGIN.getPath(), PostManager.getFirstPostId(user.getUserId()));
            response.addHeader("Location", path);
            response.addHeader("Set-Cookie", setCookieString);
        } catch (IllegalArgumentException e) {
            response.addHeader("Location", Page.LOGIN.getPath());
        }
        return response;
    }
}
