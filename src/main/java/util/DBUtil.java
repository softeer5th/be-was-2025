package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class DBUtil {
    private static Logger logger = LoggerFactory.getLogger(DBUtil.class);

    private static final String URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        logger.debug("connection = {}", connection);

        return connection;
    }

    public static void release(Connection con) {
        if(con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public static void release(Statement st, ResultSet rs){
        try{
            if(rs != null) rs.close();
            if(st != null) st.close();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
