package db.h2;

import model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class UserStorage {
    private static final UserStorage INSTANCE;

    static {
        try {
            INSTANCE = new UserStorage();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private final Connection connection = DriverManager.getConnection("jdbc:h2:mem:codestagram");

    private UserStorage() throws SQLException {
        java.lang.String createTableSQL = """
            CREATE TABLE users (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                user_id VARCHAR(255) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                name VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL
            );
        """;
        try (var stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create table: " + createTableSQL, e);
        }
    }

    public static UserStorage getInstance() { return INSTANCE; }

    public User insert(User user) {
        java.lang.String insertUserQuery = """
            INSERT INTO users (user_id, password, name, email)
            VALUES (?, ?, ?, ?);
        """;

        try (var pstmt = connection.prepareStatement(insertUserQuery, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getEmail());
            pstmt.executeUpdate();
            var rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                Long generatedId = rs.getLong(1);
                return new User(generatedId, user.getUserId(), user.getPassword(), user.getName(), user.getEmail());
            } else {
                throw new SQLException("Creating user failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save user: " + user.getUserId(), e);
        }
    }

    public void update(User user) {
        java.lang.String updateUserQuery = """
                    UPDATE users SET name = ?, password = ?
                """;

        try(var pstmt = connection.prepareStatement(updateUserQuery)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getPassword());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user: " + user, e);
        }
    }

    public User findUserById(java.lang.String userId) {
        java.lang.String sql = "SELECT id, user_id, password, name, email FROM users WHERE user_id = ?";

        try (var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            var rs = pstmt.executeQuery();

            if (rs.next()) {
                Long lond = rs.getLong("id");
                return new User(
                        rs.getLong("id"),
                        rs.getString("user_id"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("email")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user: " + userId, e);
        }
        return null;
    }
}
