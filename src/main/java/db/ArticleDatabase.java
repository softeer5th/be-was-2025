package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import dto.Cursor;
import model.Article;

public class ArticleDatabase {
	private static final DataSource dataSource = DataSource.getInstance();
	private static final AtomicInteger index = new AtomicInteger(1);
	private static ArticleDatabase INSTANCE;

	private ArticleDatabase() {
		createTable();
	}

	public static ArticleDatabase getInstance() {
		if (INSTANCE == null) {
			synchronized (ArticleDatabase.class) {
				if (INSTANCE == null) {
					INSTANCE = new ArticleDatabase();
				}
			}
		}
		return INSTANCE;
	}
	private static void createTable() {
		String dropTableQuery = "DROP TABLE IF EXISTS ARTICLE";

		String createTableQuery = """
			CREATE TABLE IF NOT EXISTS ARTICLE (
			    id int PRIMARY KEY,
			    title VARCHAR(255),
			    content TEXT,
			    user_id VARCHAR(255)
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

	public void save(Article article) {
		String query = """
  		INSERT INTO ARTICLE (id, title, content, user_id) 
  		VALUES (?, ?, ?, ?);
  		""";

		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, index.getAndIncrement());
			statement.setString(2, null);
			statement.setString(3, article.getContent());
			statement.setString(4, article.getUserId());
			statement.execute();
			// 커밋단계

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 지정된 n번째 게시물과 그 다음 게시물을 조회하여 반환합니다.
	 *
	 * @param n 조회할 게시물의 번호
	 * @return 해당 게시물과 페이지 정보를 담은 Cursor 객체
	 * @throws RuntimeException SQL 오류가 발생하면 예외가 던져집니다.
	 */
	public Cursor<Article> findNthArticle(Integer n) {
		String query = """
        SELECT id, title, content, user_id 
        FROM ARTICLE
        ORDER BY id DESC
    	LIMIT 2 OFFSET ?;
    """;

		try (Connection connection = dataSource.getConnection();
			 PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, n - 1);  // OFFSET은 0부터 시작하므로 n - 1을 사용

			ResultSet resultSet = statement.executeQuery();

			if (!resultSet.next()) {
				throw new IllegalArgumentException("N is out of bounds");
			}

			// 결과를 Article 객체로 변환
			Article article = new Article(
				resultSet.getInt("id"),
				resultSet.getString("title"),
				resultSet.getString("content"),
				resultSet.getString("user_id")
			);

			boolean hasPrevPage = n > 1;
			boolean hasNextPage = false;
			if (resultSet.next()) {
				hasNextPage = true;
			}

			Integer prevPage = hasPrevPage ? n - 1 : 0;
			Integer nextPage = hasNextPage ? n + 1 : 0;

			return new Cursor<>(Optional.of(article), prevPage, nextPage, hasPrevPage, hasNextPage);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
