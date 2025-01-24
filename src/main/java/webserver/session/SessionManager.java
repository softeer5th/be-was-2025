package webserver.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private final Map<String, HttpSession> SESSION_POOL = new ConcurrentHashMap<>();
    private static final SessionManager SESSION_MANAGER = new SessionManager();

    private SessionManager() {}
    
    public static SessionManager getManager() {
        return SESSION_MANAGER;
    }

    public HttpSession createNewSession() {
        String sessionId = UUID.randomUUID().toString();
        while(SESSION_POOL.containsKey(sessionId)) {
            sessionId = UUID.randomUUID().toString();
        }
        HttpSession session = new HttpSession(sessionId);
        SESSION_POOL.put(sessionId, session);

        return session;
    }

    public HttpSession getSession(String sessionId) {
        if(sessionId != null && SESSION_POOL.containsKey(sessionId)) {
            return SESSION_POOL.get(sessionId);
        }

        return null;
    }

    public void invalidateSession(String sessionId) {
        SESSION_POOL.remove(sessionId);
    }
}
