package model;

public class Article {
    private Long id;
    private String content;
    private User user;

    public Article(Long id, String content, User user) {
        this.id = id;
        this.content = content;
        this.user = user;
    }

    public String getContent(){
        return this.content;
    }

    public User getUser(){
        return this.user;
    }
}
