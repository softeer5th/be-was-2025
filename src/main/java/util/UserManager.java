package util;

import db.Database;
import model.User;

public class UserManager {
    public void addUser(String parameterString) throws IllegalArgumentException {
        Parameter parameter = new Parameter(parameterString);
        String userId = parameter.getId();
        String userName = parameter.getName();
        String password = parameter.getPassword();
        String email = parameter.getEmail();

        if (Database.findUserById(userId) == null) {
            User user = new User(userId, userName, password, email);
            Database.addUser(user);
        } else throw new IllegalArgumentException("id: " + userId + " is already exists");
    }



}
