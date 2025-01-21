package model;

public class Comment {
    private static int id = 0;
    private final String commentId;
    private final String content;
    private final User user;
    private final Article article;

    public Comment(String content, User user, Article article) {
        this.commentId = String.valueOf(id);
        id += 1;
        this.content = content;
        this.user = user;
        this.article = article;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getContent() {
        return content;
    }

    public User getUser() {
        return user;
    }

    public Article getArticle() {
        return article;
    }
}
