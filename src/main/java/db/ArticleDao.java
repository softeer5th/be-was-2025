package db;

import db.transaction.Transaction;
import model.Article;
import model.User;

import java.sql.*;
import java.util.Optional;

import static util.DBUtil.getConnection;
import static util.DBUtil.release;

public class ArticleDao {
    private static final ArticleDao INSTANCE = new ArticleDao();

    private ArticleDao() {
        String sql = """
                    CREATE TABLE Article (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        content VARCHAR(255) NOT NULL,
                        user_id INT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (user_id) REFERENCES Users (id)
                            ON DELETE CASCADE
                    )
                    """;
        try{
            Connection con = getConnection();
            Statement stmt = con.createStatement();

            stmt.executeUpdate(sql);

            release(stmt, null);
        }catch (SQLException e) {
            throw new IllegalStateException("INTERNAL SERVER ERROR", e);
        }
    }
    public static ArticleDao getInstance(){
        return INSTANCE;
    }

    public Optional<Article> findArticlesWithPagination(Transaction transaction, int page, int size){
        String sql = "SELECT * FROM Article a join Users  ORDER BY a.created_at DESC LIMIT ? OFFSET ?";

        try{
            Connection con = transaction.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, size);
            pstmt.setInt(2, page * size);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(parseArticleFromResultSet(rs));
            }
            release(pstmt, rs);
        }catch(SQLException e){
            throw new IllegalStateException("INTERNAL SERVER ERROR", e);
        }

        return Optional.empty();
    }

    public void save(Transaction transaction, Long userId, Article article){
        String sql = "INSERT INTO Article(user_id, content) VALUES(?, ?)";
        try{
            Connection con = transaction.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, userId);
            pstmt.setString(2, article.getContent());

            pstmt.executeUpdate();
            release(pstmt, null);
        }catch(SQLException e){
            throw new IllegalStateException("INTERNAL SERVER ERROR", e);
        }
    }

    private Article parseArticleFromResultSet(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String content = rs.getString("content");
        Long userId = rs.getLong("user_id");
        String loginId = rs.getString("login_id");
        String password = rs.getString("password");
        String name = rs.getString("name");
        String email = rs.getString("email");
        return new Article(id, content, new User(userId, loginId, password, name, email));
    }


}
