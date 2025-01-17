package db;

import model.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionDB {
    private static final Map<String, User> sessionStore = new ConcurrentHashMap<>();

    public static void saveSession(String sessionId, User user) {
        sessionStore.put(sessionId, user);
    }

    public static User getUser(String sessionId) {
        return sessionStore.get(sessionId);
    }

    public static void removeSession(String sessionId) {
        sessionStore.remove(sessionId);
    }
}