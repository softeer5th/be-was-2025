package model;

import java.time.LocalTime;

public class Article {
    private int id;
    private String content;
    private String authorId;
    private String authorName;
    private LocalTime createTime;

    public Article(int id, String content, String authorId, String authorName, LocalTime createTime) {
        this.id = id;
        this.content = content;
        this.authorId = authorId;
        this.authorName = authorName;
        this.createTime = createTime;
    }

    public Article(String content, String authorId, String authorName, LocalTime createTime) {
        this.content = content;
        this.authorId = authorId;
        this.authorName = authorName;
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public LocalTime getCreateTime() {
        return createTime;
    }
}
