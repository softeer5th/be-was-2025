package webserver.session;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionStorage {
    private static Map<String, Map<String, String>> storage = new ConcurrentHashMap<>();

    public static Map<String, String> getStorage(String sessionId) {
        return storage.get(sessionId);
    }

    public static Map<String, String> setSession(String sessionId) {
        if (!storage.containsKey(sessionId)) {
            storage.put(sessionId, new HashMap<>());
        }
        return storage.get(sessionId);
    }

    public static void clearSession(String sessionId) {
        storage.remove(sessionId);
    }
}
