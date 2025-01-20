package model;

public class Post {
    private int postId;
    private String userId;
    private String title;
    private String content;

    public Post(String userId, String title, String content) {
        this.userId = userId;
        this.title = title;
        this.content = content;
    }

    public Post(int postId, String userId, String title, String content) {
        this.postId = postId;
        this.userId = userId;
        this.title = title;
        this.content = content;
    }

    public int getPostId() {
        return postId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
