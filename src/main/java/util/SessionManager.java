package util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SessionManager {
    public Map<String, String> sessionMap;
    private static final String SID = "SID";
    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int STRING_LENGTH = 5;

    public SessionManager() {
        this.sessionMap = new HashMap<>();
    }

    public String makeAndSaveSessionId(String userId) {
        Random random = new Random();
        StringBuilder sessionBuilder = new StringBuilder(STRING_LENGTH);

        for (int i = 0; i < STRING_LENGTH; i++) {
            int randomIndex = random.nextInt(CHAR_POOL.length());
            sessionBuilder.append(CHAR_POOL.charAt(randomIndex));
        }

        final String sessionId = sessionBuilder.toString();

        sessionMap.put(userId, sessionId);
        return sessionId;
    }

    public String makeHeaderValueWithSession(String sessionId) {
        return String.format("%s=%s; Path=/", SID, sessionId);
    }
}
