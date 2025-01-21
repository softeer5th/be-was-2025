package db;

public interface SessionDataManager {
    void saveSession(String sessionID, String userId);

    String findUserBySessionID(String sessionID);

    void removeSession(String sessionID);

    void clear();

    void setSessionExpire(String sessionId, long expires);
}
