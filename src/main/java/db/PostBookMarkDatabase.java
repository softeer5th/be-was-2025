package db;

import exception.ServerErrorException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static exception.ErrorCode.ERROR_WITH_DATABASE;

public class PostBookMarkDatabase {
    private static PostBookMarkDatabase instance;

    private PostBookMarkDatabase() {
    }

    public static PostBookMarkDatabase getInstance() {
        if (instance == null) {
            instance = new PostBookMarkDatabase();
        }
        return instance;
    }

    public void addBookMark(int postId, int userId) {
        String query = "INSERT INTO post_mark(post_id, user_id) VALUES (?, ?)";

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

    public boolean existsBookMark(int postId, int userId) {
        String query = "SELECT * FROM post_mark WHERE post_id = ? AND user_id = ?";
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
