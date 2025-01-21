package model;

public class Comment {
    private int id;
    private String userId;
    private int postId;
    private String content;

    public Comment(int id, String userId, int postId, String content) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public int getPostId() {
        return postId;
    }

    public String getContent() {
        return content;
    }
}

