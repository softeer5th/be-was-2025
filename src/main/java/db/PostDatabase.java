package db;

import exception.ServerErrorException;
import model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.Instant;

import static exception.ErrorCode.ERROR_WITH_DATABASE;

/**
 * 게시물 관리와 관련된 데이터베이스 접근 객체입니다.
 * 게시물 추가, 조회, 페이지 수 계산 등의 기능을 제공합니다.
 */
public class PostDatabase {
    private static final Logger logger = LoggerFactory.getLogger(PostDatabase.class);
    private static PostDatabase instance;
    private static final String TABLE_NAME = "post"; // 게시물이 저장된 데이터베이스 테이블 이름
    private static final int PAGE_SIZE = 1; // 한 페이지에 보여줄 게시물 수

    private PostDatabase() {
    }

    /**
     * Singleton 패턴으로 PostDatabase 객체를 반환합니다.
     *
     * @return {@link PostDatabase} 인스턴스
     */
    public static PostDatabase getInstance() {
        if (instance == null) {
            instance = new PostDatabase();
        }
        return instance;
    }

    /**
     * 새로운 게시물을 데이터베이스에 추가합니다.
     *
     * @param post 추가할 게시물 정보
     * @return 추가된 게시물 ID
     * @throws ServerErrorException 데이터베이스 작업 중 오류 발생 시 예외를 던집니다.
     */
    public int addPost(Post post) {
        String query = String.format("INSERT INTO %s (content, created_at, author) VALUES (?, ? ,?)", TABLE_NAME);

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, post.getContents());
            pstmt.setTimestamp(2, Timestamp.from(Instant.now()));
            pstmt.setString(3, URLDecoder.decode(post.getAuthor(), StandardCharsets.UTF_8));

            final int id = pstmt.executeUpdate();
            logger.debug("Add post: " + post);
            return id;

        } catch (SQLException e) {
            throw new ServerErrorException(ERROR_WITH_DATABASE);
        }
    }

    /**
     * 특정 페이지의 게시물을 가져옵니다.
     *
     * @param page 요청한 페이지 번호 (1부터 시작)
     * @return 해당 페이지의 게시물 객체. 게시물이 없으면 null을 반환합니다.
     * @throws ServerErrorException 데이터베이스 작업 중 오류 발생 시 예외를 던집니다.
     */
    public Post getPost(int page) {
        if (page < 1) {
            page = 1;
        }

        try (Connection conn = DBConnectionManager.getConnection()) {
            String query = String.format("SELECT * FROM %s ORDER BY id DESC LIMIT ? OFFSET ?", TABLE_NAME);
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, PAGE_SIZE);
            pstmt.setInt(2, page - 1);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Post post = new Post(
                            rs.getInt("id"),
                            rs.getString("content"),
                            rs.getString("author"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                    logger.debug("Fetched post: " + post);
                    return post;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new ServerErrorException(ERROR_WITH_DATABASE);
        }
    }

    /**
     * 게시물 총 페이지 수를 계산합니다.
     *
     * @return 게시물 총 페이지 수
     * @throws ServerErrorException 데이터베이스 작업 중 오류 발생 시 예외를 던집니다.
     */
    public int getTotalPages() {
        String query = String.format("SELECT COUNT(*) AS total_count FROM %s", TABLE_NAME);

        try (Connection conn = DBConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                int totalCount = rs.getInt("total_count");
                return (int) Math.ceil((double) totalCount / PAGE_SIZE);
            }
            return 0;
        } catch (SQLException e) {
            throw new ServerErrorException(ERROR_WITH_DATABASE);
        }
    }
}
