package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database implements TransactionFactory {

    private final String jdbcUrl;
    private final String username;
    private final String password;

    public Database(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;

    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Transaction createTransaction() {
        return new Transaction(getConnection());
    }
}
