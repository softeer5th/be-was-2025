package db;

import model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Database {
    private static Map<String, User> users = new HashMap<>();

    public static void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public static Optional<User> findUserById(String userId) {
        User orDefault = users.getOrDefault(userId, null);
        return Optional.ofNullable(orDefault);
    }

    public static Collection<User> findAll() {
        return users.values();
    }
}
