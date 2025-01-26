package db;

import model.User;

import java.util.Collection;

public interface UserDataManager {
    void addUser(User user);

    User findUserById(String userId);

    Collection<User> findAll();

    void clear();
}
