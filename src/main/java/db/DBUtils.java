package db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class DBUtils {
    private static final String URL = "jdbc:h2:tcp://localhost/~/test";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";
    private static final Logger log = LoggerFactory.getLogger(DBUtils.class);

    static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

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
