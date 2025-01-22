package db;

import db.connection.ConnectionProvider;
import model.Post;
import model.User;
import webserver.exception.HTTPException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class Database {
    private final ConnectionProvider provider;

    public Database(ConnectionProvider provider) {
        this.provider = provider;
        initTable();
    }

    public void initTable() {
        try (Connection conn = provider.getConnection()) {
            Tables.createUserTable(conn);
            Tables.createPostTable(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new HTTPException.Builder()
                    .causedBy(Database.class)
                    .internalServerError(e.getMessage());
        }
    }

    public Optional<User> findUserById(String userId) {
        try (Connection connection = provider.getConnection()) {
            String sql = "SELECT * FROM users WHERE id = ?";
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

    public void addUser(User user) {
        try (Connection connection = provider.getConnection()) {
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

    public void enrollPost(String title, String body) {
        try (Connection connection = provider.getConnection()) {
            String sql = "INSERT INTO posts (title, body) VALUES (?, ?)";
            PreparedStatement ptmt = connection.prepareStatement(sql);
            ptmt.setString(1, title);
            ptmt.setString(2, body);
            ptmt.execute();
        } catch (SQLException e) {
            throw new HTTPException.Builder()
                    .causedBy(Database.class)
                    .internalServerError(e.getMessage());
        }
    }

    public Optional<Post> getPost(String userId, int page) {
        try (Connection connection = provider.getConnection()) {
            String sql = "SELECT * FROM posts WHERE user_id = ? LIMIT 1 OFFSET ?";
            PreparedStatement ptmt = connection.prepareStatement(sql);
            ptmt.setString(1, userId);
            ptmt.setInt(2, page);
            ResultSet rs = ptmt.executeQuery();
            if (!rs.next()) {
                return Optional.empty();
            }
            int postId = rs.getInt("id");
            String title = rs.getString("title");
            String body = rs.getString("body");
            return Optional.of(new Post(postId, title, body));
        } catch (SQLException e) {
            throw new HTTPException.Builder()
                    .causedBy(Database.class)
                    .internalServerError(e.getMessage());
        }
    }

    public Collection<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Connection conn = provider.getConnection()) {
            String sql = "SELECT * FROM users";
            PreparedStatement ptmt = conn.prepareStatement(sql);
            ResultSet rs = ptmt.executeQuery();
            while (rs.next()) {
                String userId = rs.getString("id");
                String name = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                User user = new User(userId, name, password, email);
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            throw new HTTPException.Builder()
                    .causedBy(Database.class)
                    .internalServerError(e.getMessage());
        }
    }
}
