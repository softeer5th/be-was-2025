package http.session;

import model.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SessionManager {
    private static final SessionManager INSTANCE = new SessionManager();
    private static final Map<String, Session> sessionStore = new ConcurrentHashMap<>();
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    public static SessionManager getInstance(){
        return INSTANCE;
    }

    private SessionManager(){}

    public Session createSession(){
        Session session = new Session();
        sessionStore.put(session.getSessionId(), session);
        scheduler.schedule(() -> {
            sessionStore.remove(session.getSessionId());
            }, Session.MAX_INACTIVE_INTERVAL, TimeUnit.MICROSECONDS);

        return session;
    }

    public void removeSession(String sessionId){
        sessionStore.remove(sessionId);
    }

    public Object getSessionAttribute(String sessionId, String attributeName){
        Session session = sessionStore.get(sessionId);

        return session.getAttribute(attributeName);
    }
}
