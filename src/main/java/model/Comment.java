package model;

public class Comment {
    public Long id = null;
    public User user;
    public Article article;
    public java.lang.String content;

    public Comment(Long id, User user, Article article, java.lang.String content) {
        this.id = id;
        this.content = content;
        this.user = user;
        this.article = article;
    }

    public Comment(User user, Article article, java.lang.String content) {
        this.content = content;
        this.user = user;
        this.article = article;
    }

    public Long getId() { return id; }

    public User getUser() { return user; }

    public java.lang.String getContent() { return content; }

    public Article getArticle() { return article; }
}
