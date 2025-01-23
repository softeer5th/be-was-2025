package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 데이터베이스 연결을 담당하는 클래스
 */
public class Database implements TransactionFactory {

    private final String jdbcUrl;
    private final String username;
    private final String password;

    /**
     * 데이터베이스 연결 정보를 받아 데이터베이스 객체를 생성한다.
     *
     * @param jdbcUrl  데이터베이스 URL
     * @param username 데이터베이스 사용자 이름
     * @param password 데이터베이스 비밀번호
     */
    public Database(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;

    }

    /**
     * 새로운 데이터베이스 연결을 반환한다.
     */
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
