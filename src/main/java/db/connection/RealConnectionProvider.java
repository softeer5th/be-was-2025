package db.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class RealConnectionProvider implements ConnectionProvider {
    private static final String DB_URL = "jdbc:h2:~/real";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    public RealConnectionProvider() {
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
