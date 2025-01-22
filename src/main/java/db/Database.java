package db;

import model.*;
import webserver.HTTPExceptions;
import model.Session;

import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class Database {
    private static Map<String, User> users = new ConcurrentHashMap<>();
    private static Map<String, Session> sessions = new ConcurrentHashMap<>();
    private static ConcurrentSkipListMap<String, Article> articles = new ConcurrentSkipListMap<>();

    public static void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public static void addSession(Session session) {
        sessions.put(session.getSessionId(), session);
    }

    public static void addArticle(Article article) {
        articles.put(article.getArticleId(), article);
    }

    public static User getUserById(String userId) {
        return users.get(userId);
    }

    public static Session getSessionById(String sessionId) {
        return sessions.get(sessionId);
    }

    public static int getSessionMaxInactiveInterval(String sessionId) {
        Session session = getSessionById(sessionId);

        if (session == null) {
            throw new HTTPExceptions.Error403("session not found");
        }

        return session.getMaxInactiveInterval();
    }

    public static void updateSessionLastAccessTimeToNow(String sessionId) {
        Session session = getSessionById(sessionId);

        if (session == null) {
            throw new HTTPExceptions.Error403("session not found");
        }

        LocalTime time = LocalTime.now();
        session.setLastAccessTime(time);

    }

    public static void deleteSession(String sessionId) {
        sessions.remove(sessionId);
    }
}
