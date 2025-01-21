package domain;

import db.AbstractDao;
import db.Database;
import db.Transaction;
import db.TransactionalDao;
import util.ReflectionUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * 게시글 정보를 데이터베이스에 저장하거나 조회하는 클래스
 */
public class ArticleDao extends AbstractDao implements TransactionalDao<ArticleDao> {
    private static final String INSERT_ARTICLE = """
            INSERT INTO articles (writerId, content)
            VALUES (?, ?)
            """;
    private static final String SELECT_ARTICLE_BY_ID = """
            SELECT *
            FROM articles
            LEFT JOIN users ON articles.writerId = users.userId
            WHERE articleId = ?
            """;
    private static final String SELECT_LATEST_ARTICLE_ID = """
            SELECT articleId
            FROM articles
            ORDER BY articleId DESC
            LIMIT 1
            """;
    private static final String SELECT_NEXT_ARTICLE_ID = """
            SELECT articleId
            FROM articles
            WHERE articleId > ?
            ORDER BY articleId DESC
            LIMIT 1
            """;
    private static final String SELECT_PREVIOUS_ARTICLE_ID = """
            SELECT articleId
            FROM articles
            WHERE articleId < ?
            ORDER BY articleId DESC
            LIMIT 1
            """;
    private final Database database;

    /**
     * ArticleDao 객체를 생성한다.
     *
     * @param database 사용할 데이터베이스
     */
    public ArticleDao(Database database) {
        this.database = database;
    }

    /**
     * 게시글을 데이터베이스에 추가한다.
     *
     * @param article 추가할 게시글
     */
    public void insertArticle(Article article) {
        executeInsert(generatedId -> {
            ReflectionUtil.setField(article, "articleId", generatedId);
        }, INSERT_ARTICLE, article.getWriter().getUserId(), article.getContent());
    }

    /**
     * 게시글 번호에 해당하는 게시글을 데이터베이스에서 찾는다.
     *
     * @param articleId 찾을 게시글 번호
     * @return 게시글 번호에 해당하는 게시글. 없을 경우 빈 Optional
     */
    public Optional<Article> findArticleById(long articleId) {
        return executeQuery(rs -> {
            if (!rs.next())
                return Optional.empty();
            return Optional.of(mapArticle(rs));
        }, SELECT_ARTICLE_BY_ID, articleId);
    }

    /**
     * 가장 최근에 추가된 게시글 번호를 찾는다.
     *
     * @return 가장 최근에 추가된 게시글 번호. 없을 경우 빈 Optional
     */
    public Optional<Long> findLatestArticleId() {
        return executeQuery(rs -> {
            if (!rs.next())
                return Optional.empty();
            return Optional.of(rs.getLong(1));
        }, SELECT_LATEST_ARTICLE_ID);
    }

    /**
     * 주어진 게시글 번호보다 더 최근에 추가된 게시글 번호를 찾는다.
     *
     * @param articleId 주어진 게시글 번호
     * @return 주어진 게시글 번호보다 더 최근에 추가된 게시글 번호. 없을 경우 빈 Optional
     */
    public Optional<Long> findNextArticleId(long articleId) {
        return executeQuery(rs -> {
            if (!rs.next())
                return Optional.empty();
            return Optional.of(rs.getLong(1));
        }, SELECT_NEXT_ARTICLE_ID, articleId);
    }

    /**
     * 주어진 게시글 번호보다 더 예전에 추가된 게시글 번호를 찾는다.
     *
     * @param articleId 주어진 게시글 번호
     * @return 주어진 게시글 번호보다 더 예전에 추가된 게시글 번호. 없을 경우 빈 Optional
     */
    public Optional<Long> findPreviousArticleId(long articleId) {
        return executeQuery(rs -> {
            if (!rs.next())
                return Optional.empty();
            return Optional.of(rs.getLong(1));
        }, SELECT_PREVIOUS_ARTICLE_ID, articleId);
    }

    @Override
    public ArticleDao joinTransaction(Transaction transaction) {
        return new ArticleDao(null) {
            @Override
            protected Connection getConnection() {
                return transaction.getConnection();
            }

            @Override
            protected boolean closeConnection() {
                return false;
            }
        };
    }

    @Override
    protected Connection getConnection() {
        return database.getConnection();
    }

    @Override
    protected boolean closeConnection() {
        return true;
    }

    private Article mapArticle(ResultSet rs) throws SQLException {
        User writer = UserDao.mapUser(rs);
        return new Article(
                rs.getLong("articleId"),
                writer,
                rs.getString("content")
        );
    }
}
