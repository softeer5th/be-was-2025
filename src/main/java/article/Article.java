package article;

import user.User;

import java.util.ArrayList;
import java.util.List;

public class Article {

    private User writer;

    private String content;

    private List<Comment> comments;


    public Article(User writer, String content) {
        this.writer = writer;
        this.content = content;
        this.comments = new ArrayList<>();
    }

    public User getWriter() {
        return writer;
    }

    public String getContent() {
        return content;
    }

    public List<Comment> getComments() {
        return comments;
    }
}
