package db.h2;

import model.Article;
import model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArticleStorage {
    private static final ArticleStorage INSTANCE;

    static {
        try {
            INSTANCE = new ArticleStorage();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private final Connection connection = DriverManager.getConnection("jdbc:h2:mem:codestagram");

    private ArticleStorage() throws SQLException {
        String createTableSQL = """
            CREATE TABLE article (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                user_id BIGINT NOT NULL,
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

    public static ArticleStorage getInstance() { return INSTANCE; }

    public void insert(Article article) {
        String sql = """
            INSERT INTO article (user_id, content)
            VALUES (?, ?);
        """;
        try (var pstmt = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            pstmt.setLong(1, article.getUser().getId());
            pstmt.setString(2, article.getContent());
            pstmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                throw new RuntimeException("Failed to save article: " + article , e);
            }
        }
    }

    public Article findArticleById(Long id) {
        String selectArticleQuery = "SELECT id, user_id, content FROM article WHERE id = ?";
        String selectUserQuery = "SELECT id, user_id, password, name, email FROM users WHERE id = ?";

        try (var pstmt = connection.prepareStatement(selectArticleQuery)) {
            connection.setAutoCommit(false);
            pstmt.setLong(1, id);
            var rs = pstmt.executeQuery();

            if (rs.next()) {
                Long userId = rs.getLong("user_id");

                try (var userPstmt = connection.prepareStatement(selectUserQuery)) {
                    userPstmt.setLong(1, userId);
                    var userRs = userPstmt.executeQuery();

                    User user = null;
                    if (userRs.next()) {
                        user = new User(
                                userRs.getLong("id"),
                                userRs.getString("user_id"),
                                userRs.getString("password"),
                                userRs.getString("name"),
                                userRs.getString("email")
                        );
                    }

                    return new Article(
                            rs.getLong("id"),
                            user,  // User 객체를 포함
                            rs.getString("content")
                    );
                }
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                throw new RuntimeException("Failed to find article by ID: " + id, e);
            }
        }

        return null;
    }

    public List<Article> findAll() {
        String selectArticlesQuery = "SELECT id, user_id, content FROM article ORDER BY id DESC;";
        String selectUserQuery = "SELECT id, user_id, password, name, email FROM users WHERE id = ?";

        List<Article> articles = new ArrayList<>();

        try (var pstmt = connection.prepareStatement(selectArticlesQuery)) {
            connection.setAutoCommit(false);
            var rs = pstmt.executeQuery();

            while (rs.next()) {
                Long userId = rs.getLong("user_id");

                try (var userPstmt = connection.prepareStatement(selectUserQuery)) {
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
                        articles.add(new Article(
                                rs.getLong("id"),
                                user,
                                rs.getString("content")
                        ));

                    }
                }
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                throw new RuntimeException("Failed to find articles for user: ", e);
            }
        }

        return articles;
    }
}
