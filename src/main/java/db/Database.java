package db;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Database {
    private static Map<String, User> users = new ConcurrentHashMap<>();

    private static final String JDBC_URL = "jdbc:h2:~/codestargram";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private static final Logger logger = LoggerFactory.getLogger(Database.class);

    public static Connection getConnection() throws SQLException {
        logger.info("Connecting to database...");
        Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        return connection;
    }

    public static void addUser(User user) {
        String sql = "INSERT INTO USERS (user_id, name, email, password, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, user.getUserId());
                statement.setString(2, user.getName());
                statement.setString(3, user.getEmail());
                statement.setString(4, user.getPassword());
                statement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));

                statement.executeUpdate();

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                logger.error(e.getMessage());
                throw e;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static User findUserById(String userId) {
        String sql = "SELECT * FROM USERS WHERE user_id = ?";
        User user = null;
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, userId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                user = new User(
                        resultSet.getString("user_id"),
                        resultSet.getString("password"),
                        resultSet.getString("name"),
                        resultSet.getString("email")
                );
                logger.info("User found - ID: {}, PW: {}, NAME: {}, EMAIL: {}", user.getUserId(), user.getPassword(), user.getName(), user.getEmail());
                break;
            }

            return user;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static Collection<User> findAll() {
        String sql = "SELECT * FROM USERS";
        Collection<User> users = new ArrayList<>();

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                users.add(
                    new User(
                        resultSet.getString("user_id"),
                        resultSet.getString("password"),
                        resultSet.getString("name"),
                        resultSet.getString("email")
                    )
                );
            }

        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }

        return users;
    }
}
