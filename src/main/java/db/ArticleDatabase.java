package db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static db.UserDatabase.JDBC_URL;

public class ArticleDatabase {
    private static final Logger logger = LoggerFactory.getLogger(ArticleDatabase.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL);
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
