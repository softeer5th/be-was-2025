package model;

import java.time.LocalDateTime;

public class Comment {
    private int id;
    private final int postId;
    private final String contents;
    private final int author;
    private LocalDateTime createdAt;

    public Comment(int id,int postId, String contents, int author, LocalDateTime createdAt) {
        this.id = id;
        this.postId = postId;
        this.contents = contents;
        this.author = author;
        this.createdAt = createdAt;
    }

    public Comment(int postId, String contents, int author) {
        this.postId = postId;
        this.contents = contents;
        this.author = author;
    }

    public int getPostId() {
        return postId;
    }

    public String getContents() {
        return contents;
    }

    public int getId() {
        return id;
    }

    public int getAuthor() {
        return author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
