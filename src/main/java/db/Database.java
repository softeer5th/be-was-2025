package db;

import model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Database {
    private static Map<String, User> users = new HashMap<>();
    private static Map<String, String> sessions = new HashMap<>();

    public static void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public static User findUserById(String userId) {
        return users.get(userId);
    }

    public static boolean userExists(String userId) {return users.containsKey(userId);}

    public static Collection<User> findAll() {
        return users.values();
    }

    public static void addSession(String sessionId, String userId) {sessions.put(sessionId, userId);}

    public static String getSession(String sessionId) { return sessions.get(sessionId); }
}
