package db;

import exception.ServerErrorException;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static exception.ErrorCode.ERROR_WITH_DATABASE;

/**
 * 사용자 데이터를 관리하는 데이터베이스 접근 객체입니다.
 * 사용자 추가 및 검색과 관련된 기능을 제공합니다.
 */
public class UserDatabase {
    private static final Logger logger = LoggerFactory.getLogger(UserDatabase.class);

    private static UserDatabase instance;

    private UserDatabase() {
    }

    /**
     * Singleton 패턴으로 UserDatabase 객체를 반환합니다.
     *
     * @return {@link UserDatabase} 인스턴스
     */
    public static UserDatabase getInstance() {
        if (instance == null) {
            instance = new UserDatabase();
        }
        return instance;
    }

    /**
     * 새로운 사용자를 데이터베이스에 추가합니다.
     *
     * @param user 추가할 {@link User} 객체
     * @return 데이터베이스에 추가된 사용자 레코드의 ID
     * @throws ServerErrorException 데이터베이스 작업 중 오류 발생 시 예외를 던집니다.
     */
    public int addUser(User user) {
        String query = "INSERT INTO member (user_id, nickname, email, password) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword());

            final int id = pstmt.executeUpdate();
            logger.debug("Add user" + user);
            return id;

        } catch (SQLException e) {
            throw new ServerErrorException(ERROR_WITH_DATABASE);
        }
    }

    /**
     * 사용자 ID로 사용자를 검색합니다.
     *
     * @param id 검색할 사용자 ID
     * @return {@link Optional}로 감싸진 {@link User} 객체. 사용자가 없으면 비어 있는 Optional 반환.
     * @throws ServerErrorException 데이터베이스 작업 중 오류 발생 시 예외를 던집니다.
     */
    public Optional<User> findUserById(int id) {
        String query = "SELECT * FROM member WHERE id = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("user_id"),
                            rs.getString("password"),
                            rs.getString("nickname"),
                            rs.getString("email"),
                            rs.getString("profile")
                    );
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new ServerErrorException(ERROR_WITH_DATABASE);
        }
        return Optional.empty();
    }

    /**
     * 사용자 UserId로 사용자를 검색합니다.
     *
     * @param userId 검색할 사용자 user_id
     * @return {@link Optional}로 감싸진 {@link User} 객체. 사용자가 없으면 비어 있는 Optional 반환.
     * @throws ServerErrorException 데이터베이스 작업 중 오류 발생 시 예외를 던집니다.
     */
    public Optional<User> findUserByUserId(String userId) {
        String query = "SELECT * FROM member WHERE user_id = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("user_id"),
                            rs.getString("password"),
                            rs.getString("nickname"),
                            rs.getString("email"),
                            rs.getString("profile")
                    );
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new ServerErrorException(ERROR_WITH_DATABASE);
        }
        return Optional.empty();
    }

    public void updateProfile(int id, String filePath) {
        String query = "UPDATE member SET profile = ? WHERE id = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // filePath를 데이터베이스에 저장 (파일 경로를 저장하는 경우)
            stmt.setString(1, filePath);  // 파일 경로를 profile 컬럼에 설정
            stmt.setInt(2, id);           // 사용자 ID를 WHERE 조건에 설정

            // SQL 업데이트 실행
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new ServerErrorException(ERROR_WITH_DATABASE);
            }

        } catch (SQLException e) {
            throw new ServerErrorException(ERROR_WITH_DATABASE);
        }
    }

    public void deleteProfile(int id) {
        String query = "update member set profile = null WHERE id = ?";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);

            // SQL 업데이트 실행
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new ServerErrorException(ERROR_WITH_DATABASE);
            }

        } catch (SQLException e) {
            throw new ServerErrorException(ERROR_WITH_DATABASE);
        }
    }
}
