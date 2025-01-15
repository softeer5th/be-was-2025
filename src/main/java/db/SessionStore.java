package db;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SessionStore {
    private static ConcurrentMap<String, Object> session = new ConcurrentHashMap<>();

    public static void addSession(String sessionId, Object value) {
        session.put(sessionId, value);
    }

    public static Optional<Object> findBySessionId(String sessionId) {
        return Optional.ofNullable(session.get(sessionId));
    }
}
