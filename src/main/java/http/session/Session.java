package http.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {
    private final String sessionId;
    private final long createdTime;
    // public static final long MAX_INACTIVE_INTERVAL = 1000 * 60 * 30;

    private Map<String, Object> attributes = new HashMap<>();

    public Session() {
        sessionId = UUID.randomUUID().toString();
        createdTime = System.currentTimeMillis();
    }

    public String getSessionId(){
        return sessionId;
    }

    public boolean hasAttribute(String key){
        return attributes.containsKey(key);
    }

    public Object getAttribute(String key){
        return attributes.get(key);
    }

    public void saveAttribute(String key, Object value){
        attributes.put(key, value);
    }
}
