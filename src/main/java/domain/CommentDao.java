package domain;

import db.AbstractDao;
import db.Database;
import db.Transaction;
import db.TransactionalDao;
import util.ReflectionUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 댓글 정보를 데이터베이스에 저장하거나 조회하는 클래스
 */
public class CommentDao extends AbstractDao implements TransactionalDao<CommentDao> {
    private static final String INSERT_COMMENT = """
            INSERT INTO comments (content, writerId, articleId)
            VALUES (?, ?, ?)
            """;
    private static final String FIND_BY_ARTICLE_ID_ORDER_BY_COMMENT_ID_DESC = """
            SELECT *
            FROM comments
            LEFT JOIN users ON comments.writerId = users.userId
            WHERE articleId = ?
            ORDER BY commentId DESC
            """;

    private final Database database;

    /**
     * CommentDao 를 생성한다
     *
     * @param database 사용할 데이터베이스
     */
    public CommentDao(Database database) {
        this.database = database;
    }

    private static Comment mapComment(ResultSet rs, Article article) throws SQLException {
        User user = UserDao.mapUser(rs);
        return new Comment(rs.getLong("commentId"), user, rs.getString("content"), article);
    }

    /**
     * 댓글을 데이터베이스에 추가한다.
     *
     * @param comment 추가할 댓글
     */
    public void insertComment(Comment comment) {
        executeInsert(generatedId -> {
            ReflectionUtil.setField(comment, "commentId", generatedId);
        }, INSERT_COMMENT, comment.getContent(), comment.getWriter().getUserId(), comment.getArticle().getArticleId());
    }

    /**
     * 게시글에 달린 댓글을 조회한다.
     *
     * @param article 댓글을 조회할 게시글
     * @return 게시글에 달린 댓글 목록
     */
    public List<Comment> findAllByArticle(Article article) {
        return executeQuery(rs -> {
            List<Comment> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapComment(rs, article));
            }
            return list;
        }, FIND_BY_ARTICLE_ID_ORDER_BY_COMMENT_ID_DESC, article.getArticleId());
    }

    @Override
    public CommentDao joinTransaction(Transaction transaction) {
        return new CommentDao(null) {
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
}
