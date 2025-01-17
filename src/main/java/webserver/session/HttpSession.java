package webserver.session;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {
    public static final String SESSION_NAME = "sid";
    private final String sessionId;
    private final Map<String, Object> attributes = new HashMap<>();
    private boolean isNew;

    public HttpSession(String sessionId) {
        this.sessionId = sessionId;
        this.isNew = true;
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public void invalidate() {
        SessionManager sessionManager = SessionManager.getManager();
        sessionManager.invalidateSession(sessionId);
    }

    public String getSessionId() { return sessionId; }

    public void setNew(boolean isNew) { this.isNew = isNew; }

    public boolean isNew() { return isNew; }
}
