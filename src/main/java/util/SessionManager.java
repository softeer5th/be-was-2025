package util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {
    private final Map<String, String> sessionMap;
    private static SessionManager instance;

    private SessionManager() {
        sessionMap = new HashMap<>();
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public String makeAndSaveSessionId(final String userId) {
        final String sessionId = UUID.randomUUID().toString();

        sessionMap.put(sessionId, userId);
        return sessionId;
    }

    public void deleteSession(String sessionId) {
        sessionMap.remove(sessionId);
    }

    public String getUserId(String sessionId) {
        return sessionMap.get(sessionId);
    }
}
