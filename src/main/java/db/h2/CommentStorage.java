package db.h2;

import model.Article;
import model.Comment;
import model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommentStorage {
    private static final CommentStorage INSTANCE;

    static {
        try {
            INSTANCE = new CommentStorage();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private final Connection connection = DriverManager.getConnection("jdbc:h2:mem:codestagram");

    private CommentStorage() throws SQLException {
        String createTableSQL = """
            CREATE TABLE comments (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                user_id BIGINT NOT NULL,
                article_id BIGINT NOT NULL,
                content CLOB NOT NULL
            );
        """;
        try (var stmt = connection.createStatement()) {
            connection.setAutoCommit(false);
            stmt.execute(createTableSQL);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new RuntimeException("Failed to create table: " + createTableSQL, e);
        }
    }

    public static CommentStorage getInstance() { return INSTANCE; }

    public void insert(Comment comment) {
        String sql = """
            INSERT INTO comments (user_id, article_id, content)
            VALUES (?, ?, ?);
        """;

        try (var pstmt = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            pstmt.setLong(1, comment.getUser().getId());
            pstmt.setLong(2, comment.getArticle().getId());
            pstmt.setString(3, comment.getContent());
            pstmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                throw new RuntimeException("Failed to save comment: " + comment , e);
            }
        }
    }

    public List<Comment> findCommentsByArticle(Article article) {
        if (article.getId() == null) {
            throw new IllegalArgumentException("article id cannot be null");
        }

        String selectCommentsQuery = "SELECT id, user_id, article_id, content FROM comments WHERE article_id = ? ORDER BY id ASC;";
        String selectUserQuery = "SELECT id, user_id, email, password, name FROM users WHERE id = ?";

        List<Comment> comments = new ArrayList<>();

        try (var pstmt = connection.prepareStatement(selectCommentsQuery)) {
            pstmt.setLong(1, article.getId());
            var rs = pstmt.executeQuery();

            while (rs.next()) {
                Long userId = rs.getLong("user_id");
                try(var userPstmt = connection.prepareStatement(selectUserQuery)) {
                    userPstmt.setLong(1, userId);
                    var userRs = userPstmt.executeQuery();
                    if (userRs.next()) {
                        User user = new User(
                                userRs.getLong("id"),
                                userRs.getString("user_id"),
                                userRs.getString("password"),
                                userRs.getString("name"),
                                userRs.getString("email")
                        );

                        Comment comment = new Comment(
                                rs.getLong("id"),
                                user,
                                article,
                                rs.getString("content")
                        );

                        comments.add(comment);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find comments for article: " + article, e);
        }
        return comments;
    }
}
