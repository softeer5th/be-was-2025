package util;

import db.Database;
import model.Post;

public class PostManager {
    public static void addPost(String userId, String parameterString) {
        Parameter parameter = new Parameter(parameterString);

        String title = parameter.getValue("title");
        String content = parameter.getValue("content");

        Post post = new Post(userId, title, content);
        Database.addPost(post);
    }

    public static Post getPost(String userId, int postId) {
        return Database.getPostById(userId, postId);
    }

    public static int getFirstPostId(String userId) {
        return Database.getFirstPostId(userId);
    }

    public static String getNextPostId(String userId, int postId) {
        if(getPost(userId, postId + 1) == null) {
            return "?postId=" + postId;
        }
        return "?postId=" + (postId + 1);
    }

    public static String getPrevPostId(String userId, int postId) {
        if(getPost(userId, postId - 1) == null) {
            return "?postId=" + postId;
        }
        return "?postId=" + (postId - 1);
    }
}
