package db;

import exception.ServerErrorException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static exception.ErrorCode.ERROR_WITH_DATABASE;

public class PostLikeDatabase {
    private static PostLikeDatabase instance;

    private PostLikeDatabase() {
    }

    public static PostLikeDatabase getInstance() {
        if (instance == null) {
            instance = new PostLikeDatabase();
        }
        return instance;
    }

    public void addPostLike(int postId, int userId) {
        String query = "INSERT INTO post_like(post_id, user_id) VALUES (?, ?)";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, postId);
            stmt.setInt(2, userId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServerErrorException(ERROR_WITH_DATABASE);
        }
    }

    public boolean existsPostLike(int postId, int userId) {
        String query = "SELECT * FROM post_like WHERE post_id = ? AND user_id = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, postId);
            stmt.setInt(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServerErrorException(ERROR_WITH_DATABASE);
        }
    }

}
