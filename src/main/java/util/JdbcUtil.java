package util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class JdbcUtil {
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource dataSource;

    static {
        config.setJdbcUrl("jdbc:h2:tcp://localhost:9092/~/softeer");
        config.setUsername("sa");
        config.setPassword("");

        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void close() {
        dataSource.close();
    }
}
