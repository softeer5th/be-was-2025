package model;

import java.util.ArrayList;
import java.util.List;

public class Article {
    private static int id = 0;
    private final int articleId;
    private final String content;
    private final User user;
    private final List<Comment> comments;

    public Article(String content, User user) {
        this.articleId = id;
        id += 1;
        this.content = content;
        this.user = user;
        this.comments = new ArrayList<>();
    }

    public Article(int id, String content, User user, List<Comment> comments) {
        this.articleId = id;
        this.content = content;
        this.user = user;
        this.comments = comments;
    }

    public int getArticleId() {
        return articleId;
    }

    public String getContent() {
        return content;
    }

    public User getUser() {
        return user;
    }

    public List<Comment> getComments() {
        return comments;
    }
}
