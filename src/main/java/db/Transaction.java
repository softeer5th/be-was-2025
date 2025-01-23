package db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 하나의 데이터베이스 트랜잭션을 나타내는 클래스
 */
public class Transaction implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(Transaction.class);
    private final Connection connection;

    /**
     * 생성자
     *
     * @param connection 트랜젝션에 사용되는 JDBC Connection 객체
     */
    Transaction(Connection connection) {
        this.connection = connection;
    }

    /**
     * 트랜잭션을 시작한다.
     */
    public void begin() {
        try {
            log.debug("Begin transaction");
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
            log.debug("Commit transaction");
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
            log.debug("Rollback transaction");
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
    public void close() {
        try {
            log.debug("Close transaction");
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
