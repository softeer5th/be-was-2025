package DAO;

import model.Session;
import webserver.HTTPExceptions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.time.LocalTime;

public class SessionDAO {
    public void addSession(Session session) {
        String sql = "INSERT INTO sessions (sessionId, userId, lastAccessTime, maxInactiveInterval) VALUES (?, ?, ?, ?)";
        Connection connection = null;
        try {
            connection = H2Connection.getConnection();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, session.getSessionId());
                preparedStatement.setString(2, session.getUserId());
                preparedStatement.setTime(3, Time.valueOf(session.getLastAccessTime()));
                preparedStatement.setInt(4, session.getMaxInactiveInterval());
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

    public Session getSessionById(String sessionId) {
        String sql = "SELECT * FROM sessions WHERE sessionId = ?";
        try {
            Connection connection = H2Connection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, sessionId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Session(
                        resultSet.getString("sessionId"),
                        resultSet.getString("userId"),
                        resultSet.getTime("lastAccessTime").toLocalTime(),
                        resultSet.getInt("maxInactiveInterval")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new HTTPExceptions.Error500("Failed to get session.");
        }
        return null;
    }

    public int getSessionMaxInactiveInterval(String sessionId) {
        String sql = "SELECT * FROM sessions WHERE sessionId = ?";
        try {
            Connection connection = H2Connection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, sessionId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("maxInactiveInterval");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new HTTPExceptions.Error500("Failed to get session.");
        }
        return -1;
    }

    public void updateSessionLastAccessTime(String sessionId, LocalTime time) {
        String sql = "UPDATE sessions SET lastAccessTime = ? WHERE sessionId = ?";
        Connection connection = null;
        try {
            connection = H2Connection.getConnection();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setTime(1, Time.valueOf(time));
                preparedStatement.setString(2, sessionId);
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

    public void deleteSession(String sessionId) {
        String sql = "DELETE FROM sessions WHERE sessionId = ?";
        Connection connection = null;
        try {
            connection = H2Connection.getConnection();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, sessionId);
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

    public void deleteAllSessions() {
        String sql = "DELETE FROM sessions";
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
