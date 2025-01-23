package db;

import exception.ServerErrorException;
import model.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static exception.ErrorCode.ERROR_WITH_DATABASE;

/**
 * 댓글과 관련된 데이터베이스 작업을 관리하는 싱글톤 클래스입니다.
 * 댓글 추가 및 게시물 ID로 댓글 조회 기능을 제공합니다.
 */
public class CommentDatabase {
    private static final Logger logger = LoggerFactory.getLogger(CommentDatabase.class);

    private static CommentDatabase instance;

    /**
     * 싱글톤 패턴을 적용하기 위한 private 생성자입니다.
     */
    private CommentDatabase() {
    }

    /**
     * CommentDatabase 클래스의 싱글톤 인스턴스를 반환합니다.
     *
     * @return CommentDatabase의 싱글톤 인스턴스
     */
    public static CommentDatabase getInstance() {
        if (instance == null) {
            instance = new CommentDatabase();
        }
        return instance;
    }

    /**
     * 새로운 댓글을 데이터베이스에 추가합니다.
     *
     * @param comment 추가할 댓글 정보를 포함한 {@link Comment} 객체
     * @return 삽입된 댓글의 ID
     * @throws ServerErrorException 데이터베이스 오류가 발생한 경우
     */
    public int addComment(Comment comment) {
        String query = "INSERT INTO comment (post_id, content, created_at, author) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, comment.getPostId());
            pstmt.setString(2, URLDecoder.decode(comment.getContents(), StandardCharsets.UTF_8));
            pstmt.setTimestamp(3, Timestamp.from(Instant.now()));
            pstmt.setInt(4, comment.getAuthor());

            final int id = pstmt.executeUpdate();
            logger.debug("Add comment" + comment);
            return id;

        } catch (SQLException e) {
            throw new ServerErrorException(ERROR_WITH_DATABASE);
        }
    }

    /**
     * 특정 게시물 ID와 연관된 모든 댓글을 조회합니다.
     *
     * @param postId 댓글을 조회할 게시물의 ID
     * @return {@link Comment} 객체 리스트
     * @throws ServerErrorException 데이터베이스 오류가 발생한 경우
     */
    public List<Comment> findAllByPostId(int postId) {
        String query = "SELECT * FROM comment WHERE post_id = ? ORDER BY created_at";
        List<Comment> comments = new ArrayList<>();

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, postId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Comment comment = new Comment(
                            rs.getInt("id"),
                            rs.getInt("post_id"),
                            rs.getString("content"),
                            rs.getInt("author"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                    comments.add(comment);
                }
            }
        } catch (SQLException e) {
            throw new ServerErrorException(ERROR_WITH_DATABASE);
        }
        return comments;
    }
}
