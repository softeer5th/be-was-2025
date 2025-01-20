package webserver.session;

import java.util.HashMap;
import java.util.Map;

public class Session {
    private final String sid;
    private final Map<String, Object> attributes;

    public Session(String sid) {
        this.sid = sid;
        this.attributes = new HashMap<>();
    }

    public String getId() {
        return sid;
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public boolean isExpired(){
        return false;
    }
}