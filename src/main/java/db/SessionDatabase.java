package db;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionDatabase {
    private static Map<String, String> sessions = new ConcurrentHashMap<>();

    public static void addSession(String sessionId, String userId) {sessions.put(sessionId, userId);}

    public static String getSession(String sessionId) {return sessions.get(sessionId);}

    public static boolean sessionExists(String sessionId) { return sessions.containsKey(sessionId); }
}
