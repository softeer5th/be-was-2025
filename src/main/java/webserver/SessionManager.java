package webserver;

import global.model.SessionData;
import model.User;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final Map<String, SessionData> sessions = new ConcurrentHashMap<>();
    private static final long SESSION_TIMEOUT = 30 * 60 * 1000L;

    public static String createSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        long expirationTime = System.currentTimeMillis() + SESSION_TIMEOUT;
        sessions.put(sessionId, new SessionData(user, expirationTime));
        return sessionId;
    }

    public static User getUser(String sessionId) {
        SessionData sessionData = sessions.get(sessionId);

        if (sessionData == null || sessionData.isExpired()) {
            sessions.remove(sessionId);
            return null;
        }
        return sessionData.getUser();
    }

    public static void invalidate(String sessionId) {
        sessions.remove(sessionId);
    }

}