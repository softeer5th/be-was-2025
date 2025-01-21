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

    public static Post getPost(int postId) {
        return Database.getPostById(postId);
    }

    public static int getFirstPostId() {
        return Database.getFirstPostId();
    }

    public static String getNextPostId(int postId) {
        if(getPost(postId + 1) == null) {
            return "?postId=" + postId;
        }
        return "?postId=" + (postId + 1);
    }

    public static String getPrevPostId(int postId) {
        if(getPost(postId - 1) == null) {
            return "?postId=" + postId;
        }
        return "?postId=" + (postId - 1);
    }
}
