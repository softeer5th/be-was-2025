package db;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private static final Map<String, String> sessions = new HashMap<>(); // sessionID -> userId

    public static void saveSession(String sessionID, String userId) {
        sessions.put(sessionID, userId);
    }

    public static String findUserBySessionID(String sessionID) {
        return sessions.get(sessionID);
    }

    public static void removeSession(String sessionID) {
        sessions.remove(sessionID);
    }

}

