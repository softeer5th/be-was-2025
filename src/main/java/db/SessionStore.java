package db;

import model.Session;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SessionStore {
    private static ConcurrentMap<String, Session> session = new ConcurrentHashMap<>();

    public static void addSession(String sessionId, Session value) {
        session.put(sessionId, value);
    }

    public static Optional<Session> findBySessionId(String sessionId) {
        return Optional.ofNullable(session.get(sessionId));
    }

    public static void deleteBySessionId(String sessionId) {
        session.remove(sessionId);
    }
}
