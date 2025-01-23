package DAO;

import model.User;
import webserver.HTTPExceptions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {
    public void addUser(User user) {
        String sql = "INSERT INTO users (userID, password, name, email) VALUES (?,?,?,?)";
        Connection connection = null;
        try {
            connection = H2Connection.getConnection();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
                preparedStatement.setString(1, user.getUserId());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.setString(3, user.getName());
                preparedStatement.setString(4, user.getEmail());
                preparedStatement.executeUpdate();
            }

            connection.commit();
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (Exception rollbackException) {
                    rollbackException.printStackTrace();
                } finally {
                    throw new HTTPExceptions.Error500("Failed to commit transaction.");
                }
            }
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                    connection.close();
                } catch (Exception closeException) {
                    closeException.printStackTrace();
                    throw new HTTPExceptions.Error500("Failed to close transaction.");
                }
            }
        }
    }

    public User getUserById(String userId) {
        String sql = "SELECT * FROM users WHERE userId = ?";
        try {
            Connection connection = H2Connection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new User(
                        resultSet.getString("userId"),
                        resultSet.getString("password"),
                        resultSet.getString("name")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new HTTPExceptions.Error500("Failed to get user.");
        }
        return null;
    }

    public void deleteUserById(String userId) {
        String sql = "DELETE FROM users WHERE userId = ?";
        Connection connection = null;
        try {
            connection = H2Connection.getConnection();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, userId);
                preparedStatement.executeUpdate();
            }

            connection.commit();
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (Exception rollbackException) {
                    rollbackException.printStackTrace();
                } finally {
                    throw new HTTPExceptions.Error500("Failed to commit transaction.");
                }
            }
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                    connection.close();
                } catch (Exception closeException) {
                    closeException.printStackTrace();
                    throw new HTTPExceptions.Error500("Failed to close transaction.");
                }
            }
        }
    }

    public void deleteAllUsers() {
        String sql = "DELETE FROM users";
        Connection connection = null;
        try {
            connection = H2Connection.getConnection();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.executeUpdate();
            }

            connection.commit();
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (Exception rollbackException) {
                    rollbackException.printStackTrace();
                } finally {
                    throw new HTTPExceptions.Error500("Failed to commit transaction.");
                }
            }
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                    connection.close();
                } catch (Exception closeException) {
                    closeException.printStackTrace();
                    throw new HTTPExceptions.Error500("Failed to close transaction.");
                }
            }
        }
    }
}
