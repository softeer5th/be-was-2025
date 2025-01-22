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

    public static User updateUser(User user, String parameterString) {
        Parameter parameter = new Parameter(parameterString);
        String userName = parameter.getValue("name");
        String newPassword = parameter.getValue("password");
        String verification = parameter.getValue("password2");

        if(!newPassword.equals(verification)) {throw new IllegalArgumentException("비밀번호 확인이 다릅니다.");}

        if(newPassword.equals(user.getPassword())) {throw new IllegalArgumentException("기존 비밀번호와 같습니다.");}

        if(!userName.equals(user.getName())) {throw new IllegalArgumentException("닉네임을 확인해주세요.");}

        user.setName(userName);
        Database.updateUserPassword(user.getUserId(), newPassword);
        return user;
    }
}
