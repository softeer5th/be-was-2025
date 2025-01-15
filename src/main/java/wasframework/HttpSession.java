package wasframework;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {
    private static Map<String, String> session = new HashMap<>();

    public static void put(String key, String value) {
        session.put(key, value);
    }

    public static String get(String key) {
        return session.get(key);
    }
}
