package db;

import exception.ServerErrorException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static exception.ErrorCode.ERROR_WITH_DATABASE;

/**
 * 게시물 북마크 관리를 위한 데이터베이스 접근 객체입니다.
 * 북마크 추가 및 존재 여부 확인 기능을 제공합니다.
 */
public class PostBookMarkDatabase {
    private static PostBookMarkDatabase instance;

    private PostBookMarkDatabase() {
    }

    /**
     * Singleton 패턴으로 객체를 반환합니다.
     *
     * @return {@link PostBookMarkDatabase} 인스턴스
     */
    public static PostBookMarkDatabase getInstance() {
        if (instance == null) {
            instance = new PostBookMarkDatabase();
        }
        return instance;
    }

    /**
     * 특정 게시물(postId)에 대해 특정 사용자(userId)의 북마크를 추가합니다.
     *
     * @param postId 북마크를 추가할 게시물 ID
     * @param userId 북마크를 추가한 사용자 ID
     * @throws ServerErrorException 데이터베이스 작업 중 오류 발생 시 예외를 던집니다.
     */
    public void addBookMark(int postId, int userId) {
        String query = "INSERT INTO post_mark(post_id, user_id) VALUES (?, ?)";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, postId);
            stmt.setInt(2, userId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new ServerErrorException(ERROR_WITH_DATABASE);
        }
    }

    /**
     * 특정 게시물(postId)에 대해 특정 사용자(userId)의 북마크가 존재하는지 확인합니다.
     *
     * @param postId 확인할 게시물 ID
     * @param userId 확인할 사용자 ID
     * @return 북마크가 존재하면 true, 존재하지 않으면 false
     * @throws ServerErrorException 데이터베이스 작업 중 오류 발생 시 예외를 던집니다.
     */
    public boolean existsBookMark(int postId, int userId) {
        String query = "SELECT * FROM post_mark WHERE post_id = ? AND user_id = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, postId);
            stmt.setInt(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new ServerErrorException(ERROR_WITH_DATABASE);
        }
    }
}
