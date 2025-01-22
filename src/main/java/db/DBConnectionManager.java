package db;

import exception.ServerErrorException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static exception.ErrorCode.ERROR_WITH_DATABASE;

/**
 * 데이터베이스 연결을 관리하는 클래스입니다.
 * H2 데이터베이스를 사용하며, 연결 생성 및 닫기 기능을 제공합니다.
 */
public class DBConnectionManager {
    private static Connection connection = null;
    private static final String URL = "jdbc:h2:tcp://localhost/~/test";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    /**
     * 데이터베이스 연결을 반환합니다.
     * 기존에 연결이 없거나 닫힌 경우 새 연결을 생성합니다.
     *
     * @return {@link Connection} 데이터베이스 연결 객체
     * @throws SQLException 데이터베이스 연결 생성 실패 시 예외가 발생합니다.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                throw new ServerErrorException(ERROR_WITH_DATABASE);
            }
        }
        return connection;
    }

    /**
     * 현재 활성화된 데이터베이스 연결을 닫습니다.
     *
     * @throws SQLException 연결 닫기 실패 시 예외가 발생합니다.
     */
    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
