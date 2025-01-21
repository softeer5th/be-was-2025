package domain;

public class Comment {
    private Long commentId;
    private User writer;
    private String content;
    private Article article;

    Comment(Long commentId, User writer, String content, Article article) {
        this.commentId = commentId;
        this.writer = writer;
        this.content = content;
        this.article = article;
    }

    public static Comment create(User writer, String content, Article article) {
        return new Comment(null, writer, content, article);
    }

    public User getWriter() {
        return writer;
    }

    public String getContent() {
        return content;
    }

    public Article getArticle() {
        return article;
    }

    public Long getCommentId() {
        return commentId;
    }

    // CommentDao.saveComment() 에서 사용
    void setCommentId(Long commentId) {
        this.commentId = commentId;
    }
}
