package db;

import model.Article;
import model.Comment;
import model.User;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CommentStore {
    private static ConcurrentMap<String, Comment> comments = new ConcurrentHashMap<>();

    public static void addComment(Comment comment) {
        comments.put(comment.getCommentId(), comment);
    }

    public static Optional<Comment> findCommentById(String id) {
        return Optional.ofNullable(comments.get(id));
    }

    public static List<Comment> findAll() {
        return comments.values().stream().sorted(Comparator.comparing(Comment::getCommentId).reversed()).toList();
    }

    public static void initDb() {

        User user = new User("0", "1", "account", null);
        Article article = ArticleStore.findArticleById("0").orElseThrow(() -> new RuntimeException());
        Comment comment1 = new Comment("test comment1", user, article);
        Comment comment2 = new Comment("test comment2", user, article);
        CommentStore.addComment(comment1);
        CommentStore.addComment(comment2);
        article.getComments().add(comment1);
        article.getComments().add(comment2);
        ArticleStore.addArticle(article);
    }
}
