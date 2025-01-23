package db;

import exception.ServerErrorException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static exception.ErrorCode.ERROR_WITH_DATABASE;

/**
 * 게시물 "좋아요" 데이터를 관리하는 데이터베이스 접근 객체입니다.
 * 게시물에 대한 "좋아요" 추가 및 확인 기능을 제공합니다.
 */
public class PostLikeDatabase {
    private static PostLikeDatabase instance;

    private PostLikeDatabase() {
    }

    /**
     * Singleton 패턴으로 PostLikeDatabase 객체를 반환합니다.
     *
     * @return {@link PostLikeDatabase} 인스턴스
     */
    public static PostLikeDatabase getInstance() {
        if (instance == null) {
            instance = new PostLikeDatabase();
        }
        return instance;
    }

    /**
     * 게시물에 "좋아요"를 추가합니다.
     *
     * @param postId "좋아요"를 추가할 게시물 ID
     * @param userId "좋아요"를 추가한 사용자 ID
     * @throws ServerErrorException 데이터베이스 작업 중 오류 발생 시 예외를 던집니다.
     */
    public void addPostLike(int postId, int userId) {
        String query = "INSERT INTO post_like(post_id, user_id) VALUES (?, ?)";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, postId);
            stmt.setInt(2, userId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServerErrorException(ERROR_WITH_DATABASE);
        }
    }

    /**
     * 특정 게시물에 대해 사용자가 "좋아요"를 눌렀는지 확인합니다.
     *
     * @param postId 확인할 게시물 ID
     * @param userId 확인할 사용자 ID
     * @return 사용자가 해당 게시물에 "좋아요"를 눌렀다면 true, 아니면 false
     * @throws ServerErrorException 데이터베이스 작업 중 오류 발생 시 예외를 던집니다.
     */
    public boolean existsPostLike(int postId, int userId) {
        String query = "SELECT * FROM post_like WHERE post_id = ? AND user_id = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, postId);
            stmt.setInt(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServerErrorException(ERROR_WITH_DATABASE);
        }
    }
}
