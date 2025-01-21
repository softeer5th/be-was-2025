package db;

import util.DatabaseUtil;

/**
 * 데이터베이스 테이블 초기화를 담당하는 클래스
 */
public class DatabaseInitializer {
    private static final String CREATE_USERS_TABLE = """
            CREATE TABLE IF NOT EXISTS users (
                userId VARCHAR(20) PRIMARY KEY,
                name VARCHAR(20),
                passwordHash VARCHAR(200),
                email VARCHAR(50)
            )
            """;
    private static final String CREATE_ARTICLES_TABLE = """
            CREATE TABLE IF NOT EXISTS articles (
                articleId BIGINT AUTO_INCREMENT PRIMARY KEY,
                writerId VARCHAR(20),
                content VARCHAR(2000)
            )
            """;
    private static final String CREATE_COMMENTS_TABLE = """
            CREATE TABLE IF NOT EXISTS comments (
                commentId BIGINT AUTO_INCREMENT PRIMARY KEY,
                articleId BIGINT,
                writerId VARCHAR(20),
                content VARCHAR(500)
            )
            """;
    private static final String ADD_FOREIGN_KEY_ARTICLES = """
            ALTER TABLE articles
            ADD FOREIGN KEY (writerId) REFERENCES users(userId)
            """;
    private static final String ADD_FOREIGN_KEY_COMMENTS_ARTICLES = """
            ALTER TABLE comments
            ADD FOREIGN KEY (articleId) REFERENCES articles(articleId)
            """;
    private static final String ADD_FOREIGN_KEY_COMMENTS_USERS = """
            ALTER TABLE comments
            ADD FOREIGN KEY (writerId) REFERENCES users(userId)
            """;

    private final Database database;

    public DatabaseInitializer(Database database) {
        this.database = database;
    }

    /**
     * 데이터베이스의 테이블을 생성하고 외래키 제약 조건을 설정
     */
    public void initTables() {
        createTable();
        setForeignKeyConstraints();
    }

    private void createTable() {
        executeUpdate(CREATE_USERS_TABLE);
        executeUpdate(CREATE_COMMENTS_TABLE);
        executeUpdate(CREATE_ARTICLES_TABLE);
    }

    private void setForeignKeyConstraints() {
        executeUpdate(ADD_FOREIGN_KEY_ARTICLES);
        executeUpdate(ADD_FOREIGN_KEY_COMMENTS_ARTICLES);
        executeUpdate(ADD_FOREIGN_KEY_COMMENTS_USERS);
    }

    private void executeUpdate(String sql) {
        DatabaseUtil.run(pstmt -> pstmt, database.getConnection(), true, sql);
    }
}
