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

    public String getContent() {
        return content;
    }

}