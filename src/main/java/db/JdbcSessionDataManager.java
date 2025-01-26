package db;

import exception.BaseException;
import exception.DBErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.JdbcUtil;

import java.sql.*;

public class JdbcSessionDataManager implements SessionDataManager {
    private static final Logger logger = LoggerFactory.getLogger(JdbcSessionDataManager.class);
    private static final long EXPIRATION_TIME = 30 * 60 * 1000; // 30 minutes

    @Override
    public void saveSession(String sessionID, String userId) {
        String checkSql = "SELECT session_id FROM sessions WHERE user_id = ? AND expires_at > ?";
        String updateSql = "UPDATE sessions SET session_id = ?, expires_at = ? WHERE user_id = ?";
        String insertSql = "INSERT INTO sessions (session_id, user_id, expires_at) VALUES (?, ?, ?)";

        try (Connection conn = JdbcUtil.getConnection()) {
            conn.setAutoCommit(false);
            Timestamp newExpiresAt = new Timestamp(System.currentTimeMillis() + EXPIRATION_TIME);

            // 만료되지 않은 기존 세션이 있는지 확인
            String existingSessionID = null;
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, userId);
                checkStmt.setTimestamp(2, new Timestamp(System.currentTimeMillis())); // 현재 시간보다 만료 시간이 크면 유지됨
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        existingSessionID = rs.getString("session_id");
                    }
                }
            }

            // 기존 활성 세션이 있으면 세션과 만료시간 업데이트
            if (existingSessionID != null) {
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, sessionID);
                    updateStmt.setTimestamp(2, newExpiresAt);
                    updateStmt.setString(3, userId);
                    updateStmt.executeUpdate();
                    logger.debug("Updated session expiry: sid={}, new expires={}", existingSessionID, newExpiresAt);
                }
            }
            // 기존 세션이 없으면 새로운 세션 생성
            else {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, sessionID);
                    insertStmt.setString(2, userId);
                    insertStmt.setTimestamp(3, newExpiresAt);
                    insertStmt.executeUpdate();
                    logger.debug("New session created: sid={}, expires={}", sessionID, newExpiresAt);
                }
            }
            conn.commit();

        } catch (SQLException e) {
            handleSQLException(e);
        }
    }


    @Override
    public String findUserIdBySessionID(String sessionID) {
        String sql = "SELECT user_id, expires_at FROM Sessions WHERE session_id = ?";

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sessionID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Timestamp expiresAt = rs.getTimestamp("expires_at");
                    if (System.currentTimeMillis() > expiresAt.getTime()) {
                        removeSession(sessionID);
                        logger.debug("Session expired: sid={}", sessionID);
                        return null;
                    }
                    return rs.getString("user_id");
                }
            }

        } catch (SQLException e) {
            handleSQLException(e);
        }
        return null;
    }

    @Override
    public void removeSession(String sessionID) {
        String sql = "DELETE FROM Sessions WHERE session_id = ?";

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);
            pstmt.setString(1, sessionID);
            pstmt.executeUpdate();
            conn.commit();
            logger.debug("Session removed: sid={}", sessionID);

        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    @Override
    public void clear() {
        String sql = "DELETE FROM Sessions";

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);
            pstmt.executeUpdate();
            conn.commit();
            logger.debug("All sessions cleared.");

        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    @Override
    public void setSessionExpire(String sessionID, long expires) {
        String sql = "UPDATE Sessions SET expires_at = ? WHERE session_id = ?";

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);
            Timestamp newExpiresAt = new Timestamp(System.currentTimeMillis() + expires);

            pstmt.setTimestamp(1, newExpiresAt);
            pstmt.setString(2, sessionID);
            pstmt.executeUpdate();

            conn.commit();
            logger.debug("Session expiration updated: sid={}, expires={}", sessionID, newExpiresAt);

        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    private void handleSQLException(SQLException e) {
        DBErrorCode errorCode = DBErrorCode.mapSQLErrorCode(e);
        logger.error("Database error: SQLState={}, ErrorCode={}, Message={}",
                e.getSQLState(), e.getErrorCode(), e.getMessage());

        throw new BaseException(errorCode);
    }
}
