package webserver.session;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// 서버측 메모리에 세션을 저장하는 SessionManager
public class MemorySessionManager implements SessionManager {
    // 동시성 문제로 인해 ConcurrentHashMap 필요
    private final Map<String, HttpSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public void saveSession(String sessionId, HttpSession session) {
        sessionMap.put(sessionId, session);
    }

    @Override
    public Optional<HttpSession> getSession(String sessionId) {
        return Optional.ofNullable(sessionId).map(sessionMap::get);
    }

    @Override
    public void removeSession(String sessionId) {
        sessionMap.remove(sessionId);
    }

    @Override
    public HttpSession createAndSaveSession() {
        HttpSession session = new HttpSession(generateSessionId());
        saveSession(session.getSessionId(), session);
        return session;
    }

    private String generateSessionId() {
        return UUID.randomUUID().toString();
    }
}
