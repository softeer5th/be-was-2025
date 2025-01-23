package db;

import model.Comment;
import model.Image;
import model.Post;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String DB_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";

    static {
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {

            String createUserTable = "CREATE TABLE IF NOT EXISTS Users (" +
                    "userId VARCHAR(255) PRIMARY KEY, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "email VARCHAR(255) NOT NULL, " +
                    "profileImageId INT)";
            statement.execute(createUserTable);

            String createImageTable = "CREATE TABLE IF NOT EXISTS Images (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "userId VARCHAR(255) NOT NULL, " +
                    "contentType VARCHAR(255) NOT NULL, " +
                    "imageData MEDIUMBLOB NOT NULL, " +
                    "FOREIGN KEY (userId) REFERENCES Users(userId))";
            statement.execute(createImageTable);

            String createPostTable = "CREATE TABLE IF NOT EXISTS Posts (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "userId VARCHAR(255) NOT NULL, " +
                    "imageId INT, " +
                    "title VARCHAR(255), " +
                    "content TEXT, " +
                    "FOREIGN KEY (userId) REFERENCES Users(userId), " +
                    "FOREIGN KEY (imageId) REFERENCES Images(id))";
            statement.execute(createPostTable);

            String createCommentTable = "CREATE TABLE IF NOT EXISTS Comments (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "userId VARCHAR(255) NOT NULL, " +
                    "postId INT NOT NULL, " +
                    "content TEXT NOT NULL, " +
                    "FOREIGN KEY (userId) REFERENCES Users(userId), " +
                    "FOREIGN KEY (postId) REFERENCES Posts(id))";
            statement.execute(createCommentTable);

        } catch (SQLException e) {
            throw new RuntimeException("Error initializing database", e);
        }
    }


    public static void addUser(User user) {
        String insertQuery = "INSERT INTO Users (userId, password, name, email, profileImageId) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, user.getUserId());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getName());
            preparedStatement.setString(4, user.getEmail());
            preparedStatement.setInt(5, -1);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding user", e);
        }
    }

    public static void addPost(Post post) {
        String insertQuery = "INSERT INTO Posts (userId, imageId, title, content) VALUES (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, post.getUserId());
            preparedStatement.setInt(2, post.getImageId());
            preparedStatement.setString(3, post.getTitle());
            preparedStatement.setString(4, post.getContent());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding post", e);
        }
    }

    public static void addComment(String userId, int postId, String content) {
        String insertQuery = "INSERT INTO Comments (userId, postId, content) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, userId);
            preparedStatement.setInt(2, postId);
            preparedStatement.setString(3, content);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding comment", e);
        }
    }

    public static int addImage(String userId, String contentType, byte[] data) {
        String insertQuery = "INSERT INTO Images (userId, contentType, imageData) VALUES (?, ?, ?)";
        String countQuery = "SELECT COUNT(*) FROM Images";

        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, userId);
                preparedStatement.setString(2, contentType);
                preparedStatement.setBytes(3, data);
                preparedStatement.executeUpdate();
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(countQuery);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                } else {
                    throw new SQLException("Failed to retrieve table size.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding Image or counting rows", e);
        }
    }


    public static void updateUserPassword(String userId, String newPassword) {
        String updateQuery = "UPDATE Users SET password = ? WHERE userId = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, userId);
            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated == 0) {
                throw new RuntimeException("No user found with userId: " + userId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user password", e);
        }
    }

    public static User setUserProfile(String userId, int newProfile) {
        String updateQuery = "UPDATE Users SET profileImageId = ? WHERE userId = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setInt(1, newProfile);
            preparedStatement.setString(2, userId);
            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated == 0) {
                throw new RuntimeException("No user found with userId: " + userId);
            }

            return findUserById(userId);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user password", e);
        }
    }



    public static User findUserById(String userId) {
        String selectQuery = "SELECT * FROM Users WHERE userId = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setString(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new User(
                        resultSet.getString("userId"),
                        resultSet.getString("password"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getInt("profileImageId")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by ID", e);
        }
        return null;
    }

    public static Image getImageById(int imageId) {
        String selectQuery = "SELECT * FROM Images WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setInt(1, imageId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Image(
                        resultSet.getInt("id"),
                        resultSet.getString("userId"),
                        resultSet.getString("contentType"),
                        resultSet.getBytes("imageData")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
        return null;
    }

    public static Post getPostById(int postId) {
        String selectQuery = "SELECT * FROM Posts WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setInt(1, postId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Post(
                        resultSet.getInt("id"),
                        resultSet.getInt("imageId"),
                        resultSet.getString("userId"),
                        resultSet.getString("title"),
                        resultSet.getString("content")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
        return null;
    }

    public static int getFirstPostId() {
        String selectQuery = "SELECT id FROM Posts ORDER BY id ASC LIMIT 1";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding first post ID", e);
        }
        return -1;
    }

    public static List<Comment> findComments(int postId) {
        String selectQuery = "SELECT * FROM Comments WHERE postId = ?";
        List<Comment> comments = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setInt(1, postId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                comments.add(new Comment(
                        resultSet.getInt("id"),
                        resultSet.getString("userId"),
                        resultSet.getInt("postId"),
                        resultSet.getString("content")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding comments for postId: " + postId, e);
        }
        return comments;
    }

}
