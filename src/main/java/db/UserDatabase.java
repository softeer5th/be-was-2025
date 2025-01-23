package db;

import model.User;

import java.io.ByteArrayInputStream;
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
			    email VARCHAR(255),
			    image BLOB
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
			INSERT INTO USER (user_id, password, name, email, image) 
			VALUES (?, ?, ?, ?, ?);
  		""";

		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, user.getUserId());
			statement.setString(2, user.getPassword());
			statement.setString(3, user.getName());
			statement.setString(4, user.getEmail());

			ByteArrayInputStream imageInputStream = new ByteArrayInputStream(user.getImage());
			statement.setBlob(5, imageInputStream);
			statement.execute();

			// auto-commit 이 된다.

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	public Optional<User> findUserById(String userId) {
		String query = """
			SELECT user_id, password, name, email, image
			FROM user
			WHERE user_id = ?
  		""";

		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, userId);

			ResultSet resultSet = statement.executeQuery();

			if (!resultSet.next()) {
				return Optional.empty();
			}

			User user = new User(
				resultSet.getString("user_id"),
				resultSet.getString("password"),
				resultSet.getString("name"),
				resultSet.getString("email"),
				resultSet.getBytes("image")
			);

			return Optional.ofNullable(user);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public User findUserByIdOrThrow(String userId) {
		return findUserById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
	}

	public void updateUser(User user) {
		String query = """
        UPDATE user
        SET password = ?, name = ?, email = ?, image = ?
        WHERE user_id = ?
    """;

		try (Connection connection = dataSource.getConnection();
			 PreparedStatement statement = connection.prepareStatement(query)) {

			// 업데이트할 값들 설정
			statement.setString(1, user.getPassword());
			statement.setString(2, user.getName());
			statement.setString(3, user.getEmail());

			// 이미지가 null이 아닌 경우에만 설정 (null일 수 있음)
			if (user.getImage() != null) {
				ByteArrayInputStream imageInputStream = new ByteArrayInputStream(user.getImage());
				statement.setBlob(4, imageInputStream);
			} else {
				statement.setNull(4, java.sql.Types.BLOB);  // 이미지가 없으면 null로 설정
			}

			// user_id 설정
			statement.setString(5, user.getUserId());

			// 실행
			int rowsUpdated = statement.executeUpdate();

			if (rowsUpdated == 0) {
				throw new IllegalArgumentException("No user found with ID: " + user.getUserId());
			}

		} catch (SQLException e) {
			throw new RuntimeException("Error updating user", e);
		}
	}

}
