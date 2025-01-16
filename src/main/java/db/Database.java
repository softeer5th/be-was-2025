package db;

import model.User;
import webserver.HTTPExceptions;
import webserver.Session;

import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Database {
    private static Map<String, User> users = new HashMap<>();
    private static Map<String, Session> sessions = new HashMap<>();

    public static void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public static User findUserById(String userId) {
        return users.get(userId);
    }

    public static Collection<User> findAllUsers() {
        return users.values();
    }

    public static void addSession(Session session) {
        sessions.put(session.getSessionId(), session);
    }

    public static void deleteSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public static Session findSessionById(String sessionId) {
        return sessions.get(sessionId);
    }

    public static LocalTime updateSessionLastAccessTime(String sessionId) {
        Session session = findSessionById(sessionId);

        if (session == null) {
            throw new HTTPExceptions.Error403("403 Forbidden: session not found");
        }

        LocalTime time = LocalTime.now();
        session.setLastAccessTime(time);

        return time;
    }

    public static int findSessionMaxInactiveInterval(String sessionId) {
        Session session = findSessionById(sessionId);

        if (session == null) {
            throw new HTTPExceptions.Error403("403 Forbidden: session not found");
        }

        return session.getMaxInactiveInterval();
    }
}
