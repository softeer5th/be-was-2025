package DAO;

import java.sql.*;

public class H2Connection {
    private static final String JDBC_URL = "jdbc:h2:~/database";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }
}
