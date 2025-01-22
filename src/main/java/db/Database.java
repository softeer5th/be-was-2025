package db;

import model.User;
import webserver.exception.HTTPException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public class Database {
    public static Optional<User> findUserById(String userId) {
        try (Connection connection = H2Database.getConnection()) {
            String sql = "SELECT * FROM users WHERE userId = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userId);
            ResultSet rs = preparedStatement.executeQuery();
            if (!rs.next()) {
                return Optional.empty();
            }
            String username = rs.getString("username");
            String password = rs.getString("password");
            String email = rs.getString("email");
            User user = new User(userId, username, password, email);
            return Optional.of(user);
        } catch (SQLException e) {
            throw new HTTPException.Builder()
                    .causedBy(Database.class)
                    .internalServerError(e.getMessage());
        }
    }

    public static void addUser(User user) {
        try (Connection connection = H2Database.getConnection()) {
            String sql = "INSERT INTO users (id, username, password, email) VALUES (?, ?, ?, ?)";
            PreparedStatement ptmt = connection.prepareStatement(sql);
            ptmt.setString(1, user.getUserId());
            ptmt.setString(2, user.getName());
            ptmt.setString(3, user.getPassword());
            ptmt.setString(4, user.getEmail());
            ptmt.execute();
        } catch (SQLException e) {
            throw new HTTPException.Builder()
                    .causedBy(Database.class)
                    .internalServerError(e.getMessage());
        }
    }
}
