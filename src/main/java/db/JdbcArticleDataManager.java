package db;

import exception.BaseException;
import exception.DBErrorCode;
import model.Article;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JdbcArticleDataManager implements ArticleDataManger {
    private static final Logger logger = LoggerFactory.getLogger(JdbcArticleDataManager.class);

    @Override
    public void addArticle(Article article, User user) {
        String sql = "INSERT INTO Articles (userId, content) VALUES (?, ?)";

        try (Connection conn = HikariCPManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, article.getContent());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            DBErrorCode errorCode = DBErrorCode.mapSQLErrorCode(e);
            logger.error("Database error : {}", errorCode.getMessage());
            throw new BaseException(errorCode);
        }
    }

    @Override
    public Collection<Article> findArticleByUser(User user) {
        String sql = "SELECT * FROM Articles WHERE userId = ?";

        List<Article> articles = new ArrayList<>();
        try (Connection conn = HikariCPManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUserId());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    articles.add(new Article(
                            rs.getInt("id"),
                            rs.getString("userId"),
                            rs.getString("content"),
                            rs.getString("imgUrl")
                    ));
                }
            }
        } catch (SQLException e) {
            DBErrorCode errorCode = DBErrorCode.mapSQLErrorCode(e);
            logger.error("Database error : {}", errorCode.getMessage());
            throw new BaseException(errorCode);
        }
        return articles;
    }

}