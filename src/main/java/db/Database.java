package db;

import model.User;
import webserver.HTTPExceptions;
import webserver.Session;

import java.time.LocalTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Database {
    private static Map<String, User> users = new ConcurrentHashMap<>();
    private static Map<String, Session> sessions = new ConcurrentHashMap<>();

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

    public static void updateSessionLastAccessTime(String sessionId) {
        Session session = findSessionById(sessionId);

        if (session == null) {
            throw new HTTPExceptions.Error403("session not found");
        }

        LocalTime time = LocalTime.now();
        session.setLastAccessTime(time);

    }

    public static int findSessionMaxInactiveInterval(String sessionId) {
        Session session = findSessionById(sessionId);

        if (session == null) {
            throw new HTTPExceptions.Error403("session not found");
        }

        return session.getMaxInactiveInterval();
    }
}
