package db;

import exception.BaseException;
import exception.DBErrorCode;
import model.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcArticleDataManager implements ArticleDataManger {
    private static final Logger logger = LoggerFactory.getLogger(JdbcArticleDataManager.class);

    @Override
    public void addArticle(Article article) {
        logger.info("Add Article SQL");
        String sql = "INSERT INTO Articles (user_id, content) VALUES (?, ?)";

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false); // 트랜잭션 시작

            pstmt.setString(1, article.getUserId());
            pstmt.setString(2, article.getContent());
            pstmt.executeUpdate();

            conn.commit();

        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    @Override
    public Article findArticlesByUserId(String userId) {
        String sql = "SELECT * FROM Articles WHERE user_id = ? ORDER BY id DESC LIMIT 1";

        Article article = null;
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                   article = new Article(
                            rs.getInt("id"),
                            rs.getString("user_id"),
                            rs.getString("content"),
                            rs.getString("image_url")
                    );
                }
            }
            conn.commit();
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return article;
    }


    private void handleSQLException(SQLException e) {
        DBErrorCode errorCode = DBErrorCode.mapSQLErrorCode(e);
        logger.error("Database error: SQLState={}, ErrorCode={}, Message={}",
                e.getSQLState(), e.getErrorCode(), e.getMessage());

        try (Connection conn = JdbcUtil.getConnection()) {
            conn.rollback();
            logger.warn("Transaction rolled back due to an error.");
        } catch (SQLException rollbackEx) {
            logger.error("Rollback failed: {}", rollbackEx.getMessage());
        }

        throw new BaseException(errorCode);
    }
}