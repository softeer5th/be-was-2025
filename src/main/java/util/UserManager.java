package util;

import db.Database;
import model.User;

public class UserManager {
    public void addUser(String parameter) throws IllegalArgumentException {
        ParameterParser parameterParser = new ParameterParser(parameter);
        String userId = parameterParser.getId();
        String userName = parameterParser.getName();
        String password = parameterParser.getPassword();

        if (Database.findUserById(userId) == null) {
            User user = new User(userId, userName, password, "");
            Database.addUser(user);
        } else throw new IllegalArgumentException("id: " + userId + " is already exists");
    }



}
