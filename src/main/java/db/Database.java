package db;

import model.User;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * stubbing 용으로 남겨둔 Database 객체
 */
@Deprecated
public class Database {
    private static Map<String, User> users = new ConcurrentHashMap<>();

    public static void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public static User findUserById(String userId) {
        return users.get(userId);
    }

    public static Collection<User> findAll() {
        return users.values();
    }

    public static void clear(){
        users.clear();
    }
}
