package db;

import model.Article;
import model.Comment; // 추가
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String JDBC_USER = "sa";
    private static final String JDBC_PASSWORD = "";

    static {
        try {
            try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
                String createUserTableSql = """
                  CREATE TABLE IF NOT EXISTS users (
                    user_id VARCHAR(255) PRIMARY KEY,
                    password VARCHAR(255),
                    name VARCHAR(255),
                    email VARCHAR(255),
                    profile_image BLOB
                  )
                """;
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createUserTableSql);
                }

                String createArticleTableSql = """
                  CREATE TABLE IF NOT EXISTS articles (
                    id IDENTITY PRIMARY KEY,
                    user_id VARCHAR(255),
                    content CLOB,
                    image BLOB
                  )
                """;
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createArticleTableSql);
                }

                // 추가: comments 테이블
                String createCommentTableSql = """
                  CREATE TABLE IF NOT EXISTS comments (
                    id IDENTITY PRIMARY KEY,
                    article_id BIGINT,
                    user_id VARCHAR(255),
                    content CLOB
                  )
                """;
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createCommentTableSql);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addUser(User user) {
        String sql = "INSERT INTO users (user_id, password, name, email, profile_image) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getEmail());
            pstmt.setBytes(5, user.getProfileImage());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static User findUserById(String userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getString("user_id"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getBytes("profile_image")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                users.add(new User(
                        rs.getString("user_id"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getBytes("profile_image")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public static void updateUser(User user) {
        String sql = "UPDATE users SET password=?, name=?, email=?, profile_image=? WHERE user_id=?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getPassword());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getEmail());
            pstmt.setBytes(4, user.getProfileImage());
            pstmt.setString(5, user.getUserId());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addArticle(Article article) {
        String sql = "INSERT INTO articles (user_id, content, image) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, article.getUserId());
            pstmt.setString(2, article.getContent());
            pstmt.setBytes(3, article.getImage());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addComment(Comment comment) {
        String sql = "INSERT INTO comments (article_id, user_id, content) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, comment.getArticleId());
            pstmt.setString(2, comment.getUserId());
            pstmt.setString(3, comment.getContent());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Comment> findCommentsByArticleId(Long articleId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE article_id = ? ORDER BY id ASC";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, articleId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                comments.add(new Comment(
                        rs.getLong("id"),
                        rs.getLong("article_id"),
                        rs.getString("user_id"),
                        rs.getString("content")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return comments;
    }

    public static Article findArticleByPage(int page) {
        String sql = "SELECT * FROM articles ORDER BY id DESC LIMIT 1 OFFSET ?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, page - 1);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Article(
                        rs.getLong("id"),
                        rs.getString("user_id"),
                        rs.getString("content"),
                        rs.getBytes("image")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long countArticles() {
        String sql = "SELECT COUNT(*) FROM articles";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void clear() {
        String sql = "DELETE FROM users";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}