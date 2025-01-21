package handler;

import util.Parameter;
import util.PostManager;
import util.enums.HttpStatusCode;
import util.enums.Page;
import webserver.request.Request;
import webserver.response.Response;
import webserver.session.SessionManager;

public class AddPostHandler extends Handler{
    @Override
    public Response handle(Request request) {
        Response response = new Response(request, HttpStatusCode.FOUND);

        String userId = SessionManager.getSession(sessionId).getUser().getUserId();

        try {
            PostManager.addPost(userId, request.getBody());
            String path = Parameter
                    .setPostId(Page.MAIN_LOGIN.getPath(), PostManager.getFirstPostId(userId));
            response.addHeader("Location", path);
        } catch (IllegalArgumentException e) {
            response.setStatusCode(HttpStatusCode.SEE_OTHER);
            response.addHeader("Location", Page.ARTICLE.getPath());
        }

        return response;
    }
}
