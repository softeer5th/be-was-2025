package db;

import DAO.ArticleDAO;
import DAO.SessionDAO;
import DAO.UserDAO;
import model.*;
import webserver.HTTPExceptions;
import model.Session;

import java.time.LocalTime;

public class Database {
    private static UserDAO userDAO = new UserDAO();
    private static SessionDAO sessionDAO = new SessionDAO();
    private static ArticleDAO articleDAO = new ArticleDAO();

    public static void addUser(User user) {
        userDAO.addUser(user);
    }

    public static void addSession(Session session) {
        sessionDAO.addSession(session);
    }

    public static void addArticle(Article article) {
        articleDAO.addArticle(article);
    }

    public static User getUserById(String userId) {
        return userDAO.getUserById(userId);
    }

    public static Session getSessionById(String sessionId) {
        return sessionDAO.getSessionById(sessionId);
    }

    public static int getSessionMaxInactiveInterval(String sessionId) {
        int maxInactiveInterval = sessionDAO.getSessionMaxInactiveInterval(sessionId);

        if (maxInactiveInterval == -1) {
            throw new HTTPExceptions.Error403("session not found");
        }

        return maxInactiveInterval;
    }

    public static Article getLatestArticle() {
        return articleDAO.getLatestArticle();
    }

    public static Article getNextArticle(int articleId) {
        return articleDAO.getNextArticle(articleId);
    }

    public static Article getPreviousArticle(int articleId) {
        return articleDAO.getPreviousArticle(articleId);
    }

    public static void updateSessionLastAccessTime(String sessionId, LocalTime time) {
        Session session = getSessionById(sessionId);

        if (session == null) {
            throw new HTTPExceptions.Error403("session not found");
        }

        sessionDAO.updateSessionLastAccessTime(sessionId, time);
    }

    public static void deleteSession(String sessionId) {
        sessionDAO.deleteSession(sessionId);
    }
}
