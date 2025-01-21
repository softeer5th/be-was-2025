package domain;

public class Article {
    private Long articleId;

    private User writer;

    private String content;

    Article(Long articleId, User writer, String content) {
        this.articleId = articleId;
        this.writer = writer;
        this.content = content;
    }

    public static Article create(User writer, String content) {
        return new Article(null, writer, content);
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

}
