package util;

import db.Database;
import model.User;

public class UserManager {
    public static void addUser(String parameterString) {
        Parameter parameter = new Parameter(parameterString);
        String userId = parameter.getValue("id");
        String userName = parameter.getValue("name");
        String password = parameter.getValue("password");
        String email = parameter.getValue("email");

        if (Database.findUserById(userId) == null) {
            User user = new User(userId, password, userName, email);
            Database.addUser(user);
        } else throw new IllegalArgumentException("이미 존재하는 id입니다.");
    }

    public static User logIn(String parameterString) {
        Parameter parameter = new Parameter(parameterString);
        String userId = parameter.getValue("id");
        String password = parameter.getValue("password");

        User user = Database.findUserById(userId);
        if (user == null) throw new IllegalArgumentException("존재하지 않는 id입니다.");

        if (!user.getPassword().equals(password)) throw new IllegalArgumentException("비밀번호가 틀립니다.");
        return user;
    }
}
