package db.manage;

import db.Database;
import model.Post;
import util.Parameter;

public class PostManager {
    public static void addPost(String userId, int imageId, String parameterString) {
        Parameter parameter = new Parameter(parameterString);

        String title = parameter.getValue("title");
        String content = parameter.getValue("content");

        Post post = new Post(userId, imageId, title, content);
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

    public static String getNowPostId(int postId) {
        return "?postId=" + postId;
    }
}
