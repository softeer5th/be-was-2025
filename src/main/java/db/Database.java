package db;

import model.Article;
import model.Comment;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

    public static Collection<User> findAllUsers() {
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

    public static void addArticle(String userId, String content, String photo) {
        String sql = "INSERT INTO POSTS (user_id, content, photo, created_at) VALUES (?, ?, ?, ?)";

        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, userId);
                statement.setString(2, content);
                statement.setString(3, photo);
                statement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                statement.executeUpdate();

                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static Article getArticleByPageId(int pageId) {
//        String sql = "SELECT * FROM POSTS ORDER BY created_at DESC LIMIT 1 OFFSET ?";
        String sql = "SELECT * FROM POSTS WHERE id = ?";
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(sql)){

                statement.setInt(1, pageId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    return new Article(
                            resultSet.getInt("id"),
                            resultSet.getString("user_id"),
                            resultSet.getString("content"),
                            resultSet.getString("photo"),
                            resultSet.getTimestamp("created_at").toLocalDateTime()
                    );
                }
                return null;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static Article getLatestArticle() {
        String sql = "SELECT * FROM POSTS ORDER BY created_at DESC LIMIT 1";
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(sql)){
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    return new Article(
                            resultSet.getInt("id"),
                            resultSet.getString("user_id"),
                            resultSet.getString("content"),
                            resultSet.getString("photo"),
                            resultSet.getTimestamp("created_at").toLocalDateTime()
                    );
                }
                return null;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static Article getEarliestArticle() {
        String sql = "SELECT * FROM POSTS ORDER BY created_at ASC LIMIT 1";
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(sql)){
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    return new Article(
                            resultSet.getInt("id"),
                            resultSet.getString("user_id"),
                            resultSet.getString("content"),
                            resultSet.getString("photo"),
                            resultSet.getTimestamp("created_at").toLocalDateTime()
                    );
                }
                return null;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static Article getNextArticle(int pageId) {
        String sql = "SELECT * FROM posts WHERE id > ? ORDER BY id ASC LIMIT 1" ;
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, pageId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                return new Article(
                        resultSet.getInt("id"),
                        resultSet.getString("user_id"),
                        resultSet.getString("content"),
                        resultSet.getString("photo"),
                        resultSet.getTimestamp("created_at").toLocalDateTime()
                );
            }
            return null;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static Article getPreviousArticle(int pageId) {
        String sql = "SELECT * FROM posts WHERE id < ? ORDER BY id DESC LIMIT 1";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, pageId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                return new Article(
                        resultSet.getInt("id"),
                        resultSet.getString("user_id"),
                        resultSet.getString("content"),
                        resultSet.getString("photo"),
                        resultSet.getTimestamp("created_at").toLocalDateTime()
                );
            }
            return null;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static int getArticleCount() {
        String sql = "SELECT COUNT(*) FROM POSTS";
        int articleCount = 0;

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                articleCount = resultSet.getInt(1);
            }
            return articleCount;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void addComment(String userId, int postId, String comment) {
        String sql = "INSERT INTO COMMENTS (user_id, content, post_id, created_at) VALUES (?, ?, ?, ?)";
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, userId);
                statement.setString(2, comment);
                statement.setInt(3, postId);
                statement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                statement.executeUpdate();

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                logger.error(e.getMessage());
                throw e;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static List<Comment> getComments(int postId) {
        String sql = "SELECT * FROM COMMENTS WHERE post_id = ? ORDER BY created_at DESC LIMIT 3";
        List<Comment> comments = new ArrayList<>();

        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, postId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    comments.add(
                        new Comment(
                            resultSet.getInt("id"),
                            resultSet.getInt("post_id"),
                            resultSet.getString("user_id"),
                            resultSet.getString("content"),
                            resultSet.getTimestamp("created_at").toLocalDateTime()
                        )
                    );
                }
            } catch (SQLException e) {
                connection.rollback();
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }

        return comments;
    }
}
