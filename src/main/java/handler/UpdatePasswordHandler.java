package handler;

import model.User;
import db.manage.UserManager;
import util.enums.HttpStatusCode;
import util.enums.Page;
import webserver.request.Request;
import webserver.response.Response;
import webserver.session.Session;
import webserver.session.SessionManager;

public class UpdatePasswordHandler extends Handler {
    @Override
    public Response handle(Request request) {
        Response response = new Response(request, HttpStatusCode.FOUND);
        Session session = SessionManager.getSession(sessionId);
        try {
            User user = UserManager.updateUser(session.getUser(), request.getBody());
            session.updateUser(user);
            response.addHeader("Location", Page.MAIN_LOGIN.getPath());
        } catch (IllegalArgumentException e) {
            response.setStatusCode(HttpStatusCode.SEE_OTHER);
            response.addHeader("location", Page.MY_PAGE.getPath());
        }
        return response;
    }
}
