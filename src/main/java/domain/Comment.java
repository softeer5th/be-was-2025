package domain;

/**
 * 댓글을 나타내는 클래스
 */
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

    /**
     * 새로운 댓글을 생성한다.
     *
     * @param writer  댓글 작성자
     * @param content 댓글 내용
     * @param article 댓글이 달린 게시글
     * @return 생성된 댓글
     */
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
