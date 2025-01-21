package util;

import db.Database;
import model.Comment;

import java.util.List;

public class CommentManager {
    public static void addComment(String userId, int postId, String parameterString) {
        Parameter parameter = new Parameter(parameterString);
        String content = parameter.getValue("content");

        Database.addComment(userId, postId, content);
    }

    public static List<Comment> getCommentsByPost(int postId) {
        return Database.findComments(postId);
    }
}
