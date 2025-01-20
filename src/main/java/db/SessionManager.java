package db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);
    private static final long EXPIRATION_TIME = 30 * 60 * 1000;
    private static final Map<String, SessionData> sessions = new ConcurrentHashMap<>();


    public static void saveSession(String sessionID, String userId) {
        long expiresAt = System.currentTimeMillis() + EXPIRATION_TIME;
        sessions.put(sessionID, new SessionData(userId, expiresAt));
        logger.debug("Session created: sid={}, expires={}", sessionID, expiresAt);
    }

    public static String findUserBySessionID(String sessionID) {
        SessionData sessionData = sessions.get(sessionID);
        if (sessionData == null) {
            return null;
        }

        if (sessionData.isExpired()) {
            logger.debug("Session expired: sid={}", sessionID);
            removeSession(sessionID);
            return null;
        }

        return sessionData.getUserId();
    }

    public static void removeSession(String sessionID) {
        logger.debug("Removing session: sid={}", sessionID);
        sessions.remove(sessionID);
    }


    private static class SessionData {
        private final String userId;
        private final long expiresAt;

        public SessionData(String userId, long expiresAt) {
            this.userId = userId;
            this.expiresAt = expiresAt;
        }

        public String getUserId() {
            return userId;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }
    }
}

