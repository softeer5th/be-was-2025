package db;

import model.User;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Database {
    private static final Map<String, User> users = new ConcurrentHashMap<>();

    public static void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public static User findUserById(String userId) {
        if (!users.containsKey(userId)) {
            return null;
        }
        return users.get(userId);
    }

    public static Collection<User> findAll() {
        return users.values();
    }
}
