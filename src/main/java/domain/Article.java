package domain;

/**
 * 게시글 정보를 나타내는 클래스
 */
public class Article {
    private Long articleId;

    private User writer;

    private String content;

    private String articleImagePath;

    Article(Long articleId, User writer, String content, String articleImagePath) {
        this.articleId = articleId;
        this.writer = writer;
        this.content = content;
        this.articleImagePath = articleImagePath;
    }

    /**
     * 새로운 게시글을 생성한다.
     *
     * @param writer  게시글 작성자
     * @param content 게시글 내용
     * @return 생성된 게시글
     */
    public static Article create(User writer, String content, String articleImagePath) {
        return new Article(null, writer, content, articleImagePath);
    }

    public User getWriter() {
        return writer;
    }

    public String getContent() {
        return content;
    }


    public Long getArticleId() {
        return articleId;
    }

    public String getArticleImagePath() {
        return articleImagePath;
    }
}
