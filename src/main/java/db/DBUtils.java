package db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * DB 연결을 관리하는 클래스
 * DB 연결 설정이 여기에 존재한다.
 */
public class DBUtils {
    private static final String URL = "jdbc:h2:tcp://localhost/~/test";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";
    private static final Logger log = LoggerFactory.getLogger(DBUtils.class);

    /**
     * 커넥션을 획득하는 메소드
     * @return 새로 생성한 커넥션 객체
     * @throws SQLException
     */
    static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * DB 리소스를 모두 닫는 메소드
     * @param rs ResultSet 객체
     * @param stmt Statement 객체
     * @param con 커넥션 객체
     */
    static void close(ResultSet rs, Statement stmt, Connection con) {
        if(rs != null){
            try{
                if(!rs.isClosed())
                    rs.close();
            }catch(SQLException e){
                log.error("close 예외", e);
            }
        }
        if(stmt != null){
            try{
                if(!stmt.isClosed())
                    stmt.close();
            }catch(SQLException e){
                log.error("close 예외", e);
            }
        }if(con != null){
            try{
                if(!con.isClosed())
                    con.close();
            }catch(SQLException e){
                log.error("close 예외", e);
            }
        }
    }
}
