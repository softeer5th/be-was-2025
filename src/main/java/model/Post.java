package model;

import java.time.LocalDateTime;

public class Post {
    private int id;
    private final String contents;
    private final String file;
    private final String author;
    private LocalDateTime createdAt;

    public Post(int id, String contents,String file, String author, LocalDateTime createdAt) {
        this.id = id;
        this.contents = contents;
        this.file = file;
        this.author = author;
        this.createdAt = createdAt;
    }

    public Post(String contents,String file, String author) {
        this.contents = contents;
        this.file = file;
        this.author = author;
    }

    public String getContents() {
        return contents;
    }

    public String getFile() {
        return file;
    }

    public int getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", contents='" + contents + '\'' +
                ", author='" + author + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
