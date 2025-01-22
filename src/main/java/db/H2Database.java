package db;

import webserver.exception.HTTPException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2Database {
    private static final String DB_URL = "jdbc:h2:mem:test";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    public H2Database() {
        try (Connection connection = getConnection()) {
            initTables(connection);
        } catch (SQLException e) {
            throw new HTTPException.Builder()
                    .causedBy(H2Database.class)
                    .internalServerError(e.getMessage());
        }
    }

    public void initTables(Connection connection) {
        Tables.createUserTable(connection);
        Tables.createPostTable(connection);
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

}
