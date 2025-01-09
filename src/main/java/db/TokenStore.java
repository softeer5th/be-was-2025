package db;

import api.user.UserData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenStore {
    private static final Map<String, UserData> store = new ConcurrentHashMap<>();

    public static void put(String token, UserData userData) {
        store.put(token, userData);
    }

    public static UserData get(String token) {
        return store.get(token);
    }

    public static void remove(String token) {
        store.remove(token);
    }
}