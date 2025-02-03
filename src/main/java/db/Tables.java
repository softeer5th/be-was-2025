package db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Tables {
    public static void createUserTable(Connection connection) {
        String sql = "" +
                "CREATE TABLE IF NOT EXISTS users (" +
                "    id VARCHAR(255) PRIMARY KEY," +
                "    username VARCHAR(255) NOT NULL," +
                "    password VARCHAR(255) NOT NULL," +
                "    email VARCHAR(255) NOT NULL" +
                ");";
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createPostTable(Connection connection) {
        String sql = "" +
                "CREATE TABLE IF NOT EXISTS posts (" +
                "    id INT AUTO_INCREMENT PRIMARY KEY," +
                "    user_id VARCHAR(255) NOT NULL," +
                "    title VARCHAR(255) NOT NULL," +
                "    body TEXT," +
                "    FOREIGN KEY (user_id) REFERENCES users(id)" +
                ");";
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
