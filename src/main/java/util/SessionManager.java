package util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {
    private final Map<String, Integer> sessionMap;
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

    public String makeAndSaveSessionId(final int id) {
        final String sessionId = UUID.randomUUID().toString();

        sessionMap.put(sessionId, id);
        return sessionId;
    }

    public void deleteSession(String sessionId) {
        sessionMap.remove(sessionId);
    }

    public Integer getId(String sessionId) {
        return sessionMap.get(sessionId);
    }
}
