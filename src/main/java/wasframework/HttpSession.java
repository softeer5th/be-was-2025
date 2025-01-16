package wasframework;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSession {
    public static final String SESSION_ID = "SessionId";
    private static Map<String, String> session = new ConcurrentHashMap<>();

    public static void put(String key, String value) {
        session.put(key, value);
    }

    public static String get(String key) {
        return session.get(key);
    }

    public static void remove(String key){
        session.remove(key);
    }
}
