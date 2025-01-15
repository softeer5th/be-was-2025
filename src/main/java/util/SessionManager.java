package util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SessionManager {
    private final Map<String, String> sessionMap;

    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int STRING_LENGTH = 5;
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
        Random random = new Random();
        StringBuilder sessionBuilder = new StringBuilder(STRING_LENGTH);

        for (int i = 0; i < STRING_LENGTH; i++) {
            int randomIndex = random.nextInt(CHAR_POOL.length());
            sessionBuilder.append(CHAR_POOL.charAt(randomIndex));
        }

        final String sessionId = sessionBuilder.toString();

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
