package db;

import model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryDatabase {
    private static Map<String, String> sessions = new HashMap<>();

    public static void addSession(String sessionId, String userId) {sessions.put(sessionId, userId);}

    public static String getSession(String sessionId) {return sessions.get(sessionId);}

    public static boolean sessionExists(String sessionId) { return sessions.containsKey(sessionId); }
}
