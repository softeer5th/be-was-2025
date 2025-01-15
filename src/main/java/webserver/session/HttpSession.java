package webserver.session;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {
    private final String sessionId;
    private final Map<String, String> session = new HashMap<>();

    public HttpSession(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setAttribute(String key, String value) {
        session.put(key, value);
    }

    public String getAttribute(String key) {
        return session.get(key);
    }

    @Override
    public String toString() {
        return "HttpSession{" +
                "session=" + session +
                '}';
    }
}
