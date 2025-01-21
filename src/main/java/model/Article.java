package model;

import java.util.ArrayList;
import java.util.List;

public class Article {
    private static int id = 0;
    private final String articleId;
    private final String content;
    private final User user;
    private final List<Comment> comments = new ArrayList<>();

    public Article(String content, User user) {
        this.articleId = String.valueOf(id);
        id += 1;
        this.content = content;
        this.user = user;
    }

    public String getArticleId() {
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
