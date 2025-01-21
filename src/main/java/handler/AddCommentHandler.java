package handler;

import util.CommentManager;
import util.Parameter;
import util.enums.HttpStatusCode;
import util.enums.Page;
import webserver.request.Request;
import webserver.response.Response;
import webserver.session.SessionManager;

public class AddCommentHandler extends Handler{
    @Override
    public Response handle(Request request) {
        Response response = new Response(request, HttpStatusCode.FOUND);

        String userId = SessionManager.getSession(sessionId).getUser().getUserId();
        Parameter parameter = new Parameter(request.parameter);
        int postId = Integer.parseInt(parameter.getValue("postId"));

        try {
            CommentManager.addComment(userId, postId, request.getBody());
            String path = Parameter
                    .setPostId(Page.MAIN_LOGIN.getPath(), postId);
            response.addHeader("Location", path);
        } catch (RuntimeException e) {
            response.setStatusCode(HttpStatusCode.SEE_OTHER);
            String path = Parameter
                    .setPostId(Page.COMMENT.getPath(), postId);
            response.addHeader("Location", path);
        }

        return response;
    }
}
