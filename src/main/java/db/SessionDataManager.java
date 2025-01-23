package db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface SessionDataManager {
    Logger logger = LoggerFactory.getLogger(SessionDataManager.class);

    void saveSession(String sessionID, String userId);

    String findUserIdBySessionID(String sessionID);

    void removeSession(String sessionID);

    void clear();

    void setSessionExpire(String sessionId, long expires);
}
