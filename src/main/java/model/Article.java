package model;

public class Article {
    private int id;
    private String userId;
    private String content;
    private String imgUrl;

    public Article(int id, String userId, String content, String imgUrl) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.imgUrl = imgUrl;
    }

    public Article(String userId, String content, String imgUrl) {
        this.id = 0;
        this.userId = userId;
        this.content = content;
        this.imgUrl = imgUrl;
    }

    public String getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

}