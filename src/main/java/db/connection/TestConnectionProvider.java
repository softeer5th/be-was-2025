package db.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TestConnectionProvider implements ConnectionProvider {
    private static final String DB_URL = "jdbc:h2:~/";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    private final String dbName;
    public TestConnectionProvider(String dbName) {
        this.dbName = dbName;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL + dbName, DB_USER, DB_PASSWORD);
    }

    public void cleanUp() {
        String [] deleteQueries = {"DELETE FROM users", "DELETE FROM posts"};
        try(Connection conn = getConnection()) {
            for (String query : deleteQueries) {
                PreparedStatement ptmt = conn.prepareStatement(query);
                ptmt.execute();
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
