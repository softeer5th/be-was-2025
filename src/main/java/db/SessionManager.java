package db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);
    private static final Map<String, String> sessions = new HashMap<>(); // sessionID -> userId

    public static void saveSession(String sessionID, String userId) {
        sessions.put(sessionID, userId);
    }

    public static String findUserBySessionID(String sessionID) {
        return sessions.get(sessionID);
    }

    public static void removeSession(String sessionID) {
        logger.debug("Removing session: " + sessionID);
        sessions.remove(sessionID);
    }

}

