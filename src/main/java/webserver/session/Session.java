package webserver.session;

import model.User;

import java.util.HashMap;
import java.util.Map;

public class Session {
    private final String sid;
    private final Map<String, Object> attributes;

    public Session(String sid) {
        this.sid = sid;
        this.attributes = new HashMap<>();
    }

    public void updateUser(User user) {
        attributes.put("USER", user);
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

    public User getUser() {
        return (User) attributes.get("USER");
    }

    public boolean isExpired(){
        return false;
    }
}