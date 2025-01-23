package model;

public class Article {
    private Long id;
    private String userId;
    private String content;
    private byte[] image;

    public Article(Long id, String userId, String content, byte[] image) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.image = image;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public byte[] getImage() {
        return image;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }
}