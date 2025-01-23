package handler;

import db.manage.ImageManager;
import db.manage.UserManager;
import util.enums.HttpStatusCode;
import util.enums.Page;
import webserver.request.FileBody;
import webserver.request.Request;
import webserver.response.Response;
import webserver.session.Session;
import webserver.session.SessionManager;

public class ResetProfileImageHandler extends Handler{
    @Override
    public Response handle(Request request) {
        Response response = new Response(request, HttpStatusCode.FOUND);

        Session session = SessionManager.getSession(sessionId);
        String userId = session.getUser().getUserId();
        try {
            session.updateUser(UserManager.setProfile(userId, -1));
        } catch (IllegalArgumentException e) {
            response.setStatusCode(HttpStatusCode.SEE_OTHER);
        }

        response.addHeader("Location", Page.MY_PAGE.getPath());

        return response;
    }
}
