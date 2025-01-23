package model;

public class Post {
    private Integer postId;
    private String title;
    private String content;
    private String userId;

    public Post(Integer postId, String title, String content, String userId) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return String.format("Post : [\npostId={}\ntitle={}\n, content={}\n, userId={}\n", postId, title, content, userId);
    }

    public Integer getPostId() {
        return postId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getUserId() {
        return userId;
    }
}
