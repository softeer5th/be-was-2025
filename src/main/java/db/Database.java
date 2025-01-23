package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String DATABASE_URL = "jdbc:h2:~/test";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException{
        Connection conn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
        return conn;
    }

}
