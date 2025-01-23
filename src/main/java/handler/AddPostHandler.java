package handler;

import db.manage.ImageManager;
import db.manage.PostManager;
import util.enums.HttpStatusCode;
import util.enums.Page;
import webserver.request.FileBody;
import webserver.request.Request;
import webserver.response.Response;
import webserver.session.SessionManager;

public class AddPostHandler extends Handler{
    @Override
    public Response handle(Request request) {
        Response response = new Response(request, HttpStatusCode.FOUND);

        String userId = SessionManager.getSession(sessionId).getUser().getUserId();
        try {
            FileBody fileBody = null;
            for(FileBody file : request.getFiles()) {
                if(file.fieldName().equals("image")) {
                    fileBody = file;
                    break;
                }
            }

            int imageId = ImageManager.addImage(userId, fileBody);
            PostManager.addPost(userId, imageId, request.getBody());
            response.addHeader("Location", Page.MAIN_LOGIN.getPath());
        } catch (IllegalArgumentException e) {
            response.setStatusCode(HttpStatusCode.SEE_OTHER);
            response.addHeader("Location", Page.ARTICLE.getPath());
        }

        return response;
    }
}
