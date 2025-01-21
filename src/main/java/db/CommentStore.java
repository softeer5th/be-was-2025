package db;

import model.Comment;

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
}
