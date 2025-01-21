package db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Article;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.DynamicURIHandler;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class Database {
    private static final Logger logger = LoggerFactory.getLogger(Database.class);
    private static final String JDBC_URL = "jdbc:h2:/Users/songhyeonseong/Desktop/SofteerW1/softeer;AUTO_SERVER=TRUE";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL);
    }

    public static void addUser(User user) {
        String id = user.getUserId();
        String pw = user.getPassword();
        String name = user.getName();
        String email = user.getEmail();

        String sql = "INSERT INTO users (id, pw, name, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, pw);
            stmt.setString(3, name);
            stmt.setString(4, email);
            stmt.executeUpdate();
            logger.debug("H2: 사용자 저장됨 - ID: {} , 이름: {}", id, name);
        } catch (SQLException e) {
            logger.error("addUser, Error Message = {}", e.getMessage());
        }

    }

    public static Optional<User> findUserById(String id) {
        String sql = "SELECT id, pw, name, email, profile_image_path FROM users WHERE ID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User(
                        rs.getString("id"),
                        rs.getString("pw"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("profile_image_path")
                );
                logger.debug("H2: 사용자 조회 성공 - ID: {} ", id);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            logger.error("findUserById, Error Message = {}", e.getMessage());
        }
        return Optional.empty();
    }

    public static boolean userExists(String userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error("userExists, Error Message = {}", e.getMessage());
        }

        return false;
    }

    public static Collection<User> findAllUsers() {
        String sql = "SELECT id, pw, name, email, profile_image_path FROM users";
        List<User> userList = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(
                        rs.getString("id"),
                        rs.getString("pw"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("profile_image_path")
                );
                userList.add(user);
            }
        } catch (SQLException e) {
            logger.error("findAllUsers, Error Message = {}", e.getMessage());
        }

        return userList;
    }

    public static void addArticle(Article article) {
        Long articleId = article.getId();
        String userId = article.getUserId();
        String content = URLDecoder.decode(article.getContent(), StandardCharsets.UTF_8);

        String sql = "INSERT INTO ARTICLE (user_id, content) VALUES (?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, content);
            stmt.executeUpdate();
            logger.debug("H2: 게시글 저장됨 - ID: {} , 사용자 ID: {}, 내용: {}", articleId, userId, content);
        } catch (SQLException e) {
            logger.error("addArticle, Error Message = {}", e.getMessage());
        }
    }

    public static String findAllArticles() {
        String sql = "SELECT id, user_id, content, image_path FROM article ORDER BY id DESC";
        List<Article> articleList = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Article article = new Article(
                        rs.getLong("id"),
                        rs.getString("user_id"),
                        rs.getString("content"),
                        rs.getString("image_path")
                );
                articleList.add(article);
            }

            return objectMapper.writeValueAsString(articleList);

        } catch (SQLException e) {
            logger.error("findAllArticles, Error Message = {}", e.getMessage());
        } catch (JsonProcessingException e) {
            logger.error("JSON 변환 중 오류 발생: {}", e.getMessage());
        }

        return "[]"; // 오류 발생 시 빈 JSON 배열 반환
    }

}
