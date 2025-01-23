package handler;

import db.manage.ImageManager;
import db.manage.PostManager;
import db.manage.UserManager;
import util.enums.HttpStatusCode;
import util.enums.Page;
import webserver.request.FileBody;
import webserver.request.Request;
import webserver.response.Response;
import webserver.session.Session;
import webserver.session.SessionManager;

public class ModifyProfileImageHandler extends Handler {
    @Override
    public Response handle(Request request) {
        Response response = new Response(request, HttpStatusCode.FOUND);

        Session session = SessionManager.getSession(sessionId);
        String userId = session.getUser().getUserId();
        try {
            FileBody fileBody = null;
            for(FileBody file : request.getFiles()) {
                if(file.fieldName().equals("image")) {
                    fileBody = file;
                    break;
                }
            }
            int imageId = ImageManager.addImage(userId, fileBody);
            session.updateUser(UserManager.setProfile(userId, imageId));
        } catch (IllegalArgumentException e) {
            response.setStatusCode(HttpStatusCode.SEE_OTHER);
        }

        response.addHeader("Location", Page.MY_PAGE.getPath());

        return response;
    }
}
