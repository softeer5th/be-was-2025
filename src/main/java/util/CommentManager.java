package util;

import db.Database;
import model.Comment;

public class CommentManager {
    public static void addComment(String userId, int postId, String parameterString) {
        Parameter parameter = new Parameter(parameterString);
        String content = parameter.getValue("content");

        Database.addComment(userId, postId, content);
    }
}
