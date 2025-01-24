package model;

import java.time.LocalDateTime;

public class Article {
    private int id;
    private String userId;
    private String content;
    private String photo;
    private LocalDateTime createdAt;

    public Article(int id, String userId, String content, String photo, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.photo = photo;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public String getPhoto() {
        return photo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
