package db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * The type Data source.
 */
public class DataSource {
	private static DataSource INSTANCE = new DataSource();
	private Properties properties;

	private DataSource() {
		Properties properties = new Properties();

		try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
			if (input == null) {
				throw new RuntimeException("Properties file not found in classpath");
			}

			properties.load(input);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		this.properties = properties;
	}

	/**
	 * Gets instance.
	 *
	 * @return the instance
	 */
	// 싱글톤
	public static DataSource getInstance() {
		if (INSTANCE == null) {
			synchronized (DataSource.class) {
				if (INSTANCE == null) {
					INSTANCE = new DataSource();
				}
			}
		}

		return INSTANCE;
	}

	/**
	 * Gets connection.
	 *
	 * @return the connection
	 * @throws SQLException the sql exception
	 */
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(
			properties.getProperty("db.url"),
			properties.getProperty("db.user"),
			properties.getProperty("db.password"));
	}
}
