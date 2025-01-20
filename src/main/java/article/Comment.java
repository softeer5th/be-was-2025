package article;

import user.User;

public class Comment {
    private User writer;
    private String content;
    private Article article;

    public Comment(User writer, String content, Article article) {
        this.writer = writer;
        this.content = content;
        this.article = article;
        article.getComments().add(this);
    }

}
