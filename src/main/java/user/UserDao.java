package user;

import db.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static util.CommonUtil.close;

public class UserDao {
    private static final String CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS users 
            (userId VARCHAR(20), passwordHash VARCHAR(200), name VARCHAR(20), email VARCHAR(50), PRIMARY KEY(userId))
            """;
    private static final String UPSERT_USER = """
            MERGE INTO users (userId, name, passwordHash, email)
            KEY (userId)
            VALUES (?, ?, ?, ?)
            """;
    private static final String SELECT_USER_BY_ID = """
            SELECT userId, name, passwordHash, email
            FROM users
            WHERE userId = ?
            """;
    private final Database database;

    public UserDao(Database database) {
        this.database = database;
        createTable();
    }

    public boolean saveUser(User user) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(UPSERT_USER);
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getPasswordHash());
            pstmt.setString(4, user.getEmail());
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(connection, pstmt);
        }
    }

    public Optional<User> findUserById(String userId) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(SELECT_USER_BY_ID);
            pstmt.setString(1, userId);
            resultSet = pstmt.executeQuery();
            if (!resultSet.next())
                return Optional.empty();
            return Optional.of(mapUser(resultSet));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(connection, pstmt, resultSet);
        }
    }

    private void createTable() {
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(CREATE_TABLE);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(connection, pstmt);
        }
    }

    private Connection getConnection() {
        return database.getConnection();
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getString("userId"),
                resultSet.getString("passwordHash"),
                resultSet.getString("name"),
                resultSet.getString("email")
        );
    }
}
