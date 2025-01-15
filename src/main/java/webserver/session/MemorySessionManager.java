package webserver.session;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MemorySessionManager implements SessionManager {
    private final Map<String, HttpSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public void saveSession(String sessionId, HttpSession session) {
        sessionMap.put(sessionId, session);
    }

    @Override
    public Optional<HttpSession> getSession(String sessionId) {
        return Optional.ofNullable(sessionMap.get(sessionId));
    }

    @Override
    public void removeSession(String sessionId) {
        sessionMap.remove(sessionId);
    }

    @Override
    public String createSessionId() {
        return UUID.randomUUID().toString();
    }
}
