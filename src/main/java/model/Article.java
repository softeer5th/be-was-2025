package model;

public class Article {
    public Long id = null;
    public User user;
    public java.lang.String content;

    public Article(Long id, User user, java.lang.String content) {
        this.id = id;
        this.user = user;
        this.content = content;
    }

    public Article(User user, java.lang.String content) {
        this.user = user;
        this.content = content;
    }
    public Long getId() { return id; }

    public User getUser() { return user; }

    public java.lang.String getContent() { return content; }

    public void setUser(User user) { this.user = user; }
}
