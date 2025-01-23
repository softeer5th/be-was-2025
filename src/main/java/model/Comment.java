package model;

public class Comment {
    private Integer postId;
    private String userName;
    private String content;

    public Comment(Integer postId, String userName, String content) {
        this.postId = postId;
        this.userName = userName;
        this.content = content;
    }

    public Integer getPostId() {
        return postId;
    }

    public String getUserName() {
        return userName;
    }

    public String getContent() {
        return content;
    }
}
