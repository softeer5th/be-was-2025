package db;

import java.sql.Connection;

/**
 * 하나의 데이터베이스 트랜잭션을 나타내는 클래스
 */
public class Transaction implements AutoCloseable {

    private final Connection connection;

    Transaction(Connection connection) {
        this.connection = connection;
    }

    /**
     * 트랜잭션을 시작한다.
     */
    public void begin() {
        try {
            connection.setAutoCommit(false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 트랜잭션을 커밋한다.
     */
    public void commit() {
        try {
            connection.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 트랜잭션을 롤백한다.
     */
    public void rollback() {
        try {
            connection.rollback();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 현재 트랜잭션의 Connection 객체를 반환한다.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * 트랜잭션을 종료한다.
     */
    @Override
    public void close() throws Exception {
        connection.close();
    }
}
