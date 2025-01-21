package db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalSessionDataManager implements SessionDataManager {
    private static final Logger logger = LoggerFactory.getLogger(LocalSessionDataManager.class);
    private static final long EXPIRATION_TIME = 30 * 60 * 1000;
    private static final Map<String, SessionData> sessions = new ConcurrentHashMap<>();
    private static final LocalSessionDataManager instance = new LocalSessionDataManager();

    private LocalSessionDataManager() {
    }

    public static LocalSessionDataManager getInstance() {
        return instance;
    }

    public void saveSession(String sessionID, String userId) {
        long expiresAt = System.currentTimeMillis() + EXPIRATION_TIME;
        sessions.put(sessionID, new SessionData(userId, expiresAt));
        logger.debug("Session created: sid={}, expires={}", sessionID, expiresAt);
    }

    public String findUserBySessionID(String sessionID) {
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

    public void removeSession(String sessionID) {
        logger.debug("Removing session: sid={}", sessionID);
        sessions.remove(sessionID);
    }

    public void clear() {
        sessions.clear();
    }

    public void setSessionExpire(String sessionId, long expires) {
        sessions.get(sessionId).expiresAt = System.currentTimeMillis() + expires;
    }


    private static class SessionData {
        private final String userId;
        private long expiresAt;

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

