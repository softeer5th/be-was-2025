package db;

import model.User;
import exception.BaseException;
import exception.DBErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.JdbcUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JdbcUserDataManager implements UserDataManager {
    private static final Logger logger = LoggerFactory.getLogger(JdbcUserDataManager.class);

    @Override
    public void addUser(User user) {
        String sql = "INSERT INTO Users (user_id, nickname, password, email) VALUES (?, ?, ?, ?)";

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getNickname());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getEmail());

            pstmt.executeUpdate();
            conn.commit();

            logger.info("User added: user_id={}", user.getUserId());

        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    @Override
    public User findUserById(String userId) {
        String sql = "SELECT * FROM Users WHERE user_id = ?";

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getString("user_id"),
                            rs.getString("nickname"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return null;
    }

    @Override
    public Collection<User> findAll() {
        String sql = "SELECT * FROM Users";
        List<User> users = new ArrayList<>();

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                users.add(new User(
                        rs.getString("user_id"),
                        rs.getString("nickname"),
                        rs.getString("password"),
                        rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return users;
    }

    @Override
    public void clear() {
        String sql = "DELETE FROM Users";

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);
            pstmt.executeUpdate();
            conn.commit();
            logger.info("All users cleared.");

        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    private void handleSQLException(SQLException e) {
        DBErrorCode errorCode = DBErrorCode.mapSQLErrorCode(e);
        logger.error("Database error: SQLState={}, ErrorCode={}, Message={}",
                e.getSQLState(), e.getErrorCode(), e.getMessage());

        throw new BaseException(errorCode);
    }
}
