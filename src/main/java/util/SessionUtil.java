package util;

import model.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionUtil {
    private static final Map<String, User> userSessions;
    static {
        userSessions = new ConcurrentHashMap<>();
    }

    public static Map<String, User> getUserSessions() {
        return userSessions;
    }
}
