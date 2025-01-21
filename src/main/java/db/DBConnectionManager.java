package db;

import exception.ErrorCode;
import exception.ServerErrorException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static exception.ErrorCode.ERROR_WITH_DATABASE;

public class DBConnectionManager {
    private static Connection connection = null;
    private static final String URL ="jdbc:h2:tcp://localhost/~/test";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                throw new ServerErrorException(ERROR_WITH_DATABASE);
            }
        }
        return connection;
    }

    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
