package model;

public class Article {
    private Long id;
    private String content;
    private byte[] image;
    private User user;

    public Article(String content, byte[] image){
        this.content = content;
        this.image = image;
    }

    public Article(Long id, String content, byte[] image, User user) {
        this.id = id;
        this.content = content;
        this.image = image;
        this.user = user;
    }

    public String getContent(){
        return this.content;
    }

    public byte[] getImage(){
        return this.image;
    }
    public User getUser(){
        return this.user;
    }
}
