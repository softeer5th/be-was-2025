package db;

import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class UserDatabase {
	private static final DataSource dataSource = DataSource.getInstance();

	// 싱글톤 보장
	private static UserDatabase INSTANCE;

	// 생성자 private으로 접근 제한
	private UserDatabase() {
		createTable();
	}

	public static UserDatabase getInstance() {
		if (INSTANCE == null) {
			synchronized (UserDatabase.class) {
				if (INSTANCE == null) {
					INSTANCE = new UserDatabase();
				}
			}
		}
		return INSTANCE;
	}

	private static void createTable() {
		String dropTableQuery = "DROP TABLE IF EXISTS user";

		String createTableQuery = """
			CREATE TABLE IF NOT EXISTS user (
			    user_id VARCHAR(255) PRIMARY KEY,
			    password VARCHAR(255),
			    name VARCHAR(255),
			    email VARCHAR(255)
			)
			""";

		try (Connection connection = dataSource.getConnection();
			 Statement statement = connection.createStatement()) {
			statement.execute(dropTableQuery);
			statement.execute(createTableQuery);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void save(User user) {
		String query = """
			INSERT INTO USER (user_id, password, name, email) 
			VALUES (?, ?, ?, ?);
  		""";

		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, user.getUserId());
			statement.setString(2, user.getPassword());
			statement.setString(3, user.getName());
			statement.setString(4, user.getEmail());
			statement.execute();

			// auto-commit 이 된다.

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	public Optional<User> findUserById(String userId) {
		String query = """
			SELECT user_id, password, name, email
			FROM user
			WHERE user_id = ?
  		""";

		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, userId);

			ResultSet resultSet = statement.executeQuery();

			if (!resultSet.next()) {
				throw new IllegalArgumentException("N is out of bounds");
			}

			User user = new User(
				resultSet.getString("user_id"),
				resultSet.getString("password"),
				resultSet.getString("name"),
				resultSet.getString("email")
			);

			return Optional.ofNullable(user);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public User findUserByIdOrThrow(String userId) {
		return findUserById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
	}
}
