package webserver.session;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {
    private final Map<String, String> session = new HashMap<>();

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
