package db;

import model.User;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Database {
    private Map<String, User> users = new ConcurrentHashMap<>();

    public void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public User findUserById(String userId) {
        return users.get(userId);
    }

    public Collection<User> findAll() {
        return users.values();
    }
}
