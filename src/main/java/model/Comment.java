package model;

public class Comment {
    private Long id;
    private Long articleId;
    private String userId;
    private String content;

    public Comment(Long id, Long articleId, String userId, String content) {
        this.id = id;
        this.articleId = articleId;
        this.userId = userId;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public Long getArticleId() {
        return articleId;
    }

    public String getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setContent(String content) {
        this.content = content;
    }
}