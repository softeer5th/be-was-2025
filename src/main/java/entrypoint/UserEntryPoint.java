package entrypoint;

import db.Database;
import model.User;
import webserver.annotation.RequestMapping;
import webserver.annotation.RequestParam;
import webserver.enumeration.HTTPMethod;

public class UserEntryPoint {
    @RequestMapping(path = "/user/create", method = HTTPMethod.GET)
    public String signUp(
            @RequestParam(key="userId", required = true) String userId,
            @RequestParam(key="nickname", required = true) String nickname,
            @RequestParam(key="password", required = true) String password
    ) {
        if (Database.findUserById(userId) != null) {
            throw new IllegalArgumentException("Duplicated user id");
        }
        User user = new User(userId, nickname, password, "mock@mock.com");
        Database.addUser(user);
        return "SUCCESS";
    }
}
