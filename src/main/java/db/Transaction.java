package db;

import java.sql.Connection;

public class Transaction implements AutoCloseable {

    private final Connection connection;

    Transaction(Connection connection) {
        this.connection = connection;
    }


    public void begin() {
        try {
            connection.setAutoCommit(false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void commit() {
        try {
            connection.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void rollback() {
        try {
            connection.rollback();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
