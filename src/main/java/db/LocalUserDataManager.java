package db;

import model.User;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalUserDataManager implements UserDataManager {
    private static final LocalUserDataManager instance = new LocalUserDataManager();
    private final Map<String, User> users = new ConcurrentHashMap<>();

    private LocalUserDataManager() {
    }

    public static LocalUserDataManager getInstance() {
        return instance;
    }

    @Override
    public void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    @Override
    public User findUserById(String userId) {
        return users.get(userId);
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public void clear() {
        users.clear();
    }
}
