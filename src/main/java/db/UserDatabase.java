package db;

import exception.ServerErrorException;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static exception.ErrorCode.ERROR_WITH_DATABASE;

public class UserDatabase {
    private static final Logger logger = LoggerFactory.getLogger(UserDatabase.class);

    private static UserDatabase instance;

    private UserDatabase() {
    }

    public static UserDatabase getInstance() {
        if (instance == null) {
            instance = new UserDatabase();
        }
        return instance;
    }

    public int addUser(User user) {
        String query = "INSERT INTO member (user_id,nickname,email,password) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword());

            final int id = pstmt.executeUpdate();
            logger.debug("Add user" + user);
            return id;

        } catch (SQLException e) {
            throw new ServerErrorException(ERROR_WITH_DATABASE);
        }
    }

    public Optional<User> findUserById(int id) {
        String query = "SELECT * FROM member WHERE id = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(rs.getInt("id"), rs.getString("user_id"), rs.getString("password"), rs.getString("nickname"), rs.getString("email"));
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new ServerErrorException(ERROR_WITH_DATABASE);
        }
        return Optional.empty();
    }

    public Optional<User> findUserByUserId(String userId) {
        String query = "SELECT * FROM member WHERE user_id = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(rs.getInt("id"), rs.getString("user_id"), rs.getString("password"), rs.getString("nickname"), rs.getString("email"));
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new ServerErrorException(ERROR_WITH_DATABASE);
        }
        return Optional.empty();
    }
}
