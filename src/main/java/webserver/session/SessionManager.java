package webserver.session;

import model.User;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public static Session createSession(User user) {
        String sid = UUID.randomUUID().toString();
        Session session = new Session(sid);
        session.setAttribute("USER", user);
        sessions.put(sid, session);
        return session;
    }

    public static Session getSession(String sid) {
        return sessions.get(sid);
    }

    public static void removeSession(String sid) {
        sessions.remove(sid);
    }

    public static void clearExpiredSessions() {
    }
}