package entrypoint;

import db.Database;
import model.User;
import webserver.annotation.Body;
import webserver.annotation.RequestMapping;
import webserver.enumeration.HTTPMethod;
import webserver.exception.HTTPException;

public class UserEntryPoint {
    @RequestMapping(path = "/user/create", method = HTTPMethod.POST)
    public String signUp(
            @Body(key="userId") String userId,
            @Body(key="nickname") String nickname,
            @Body(key="password") String password
    ) {
        if (Database.findUserById(userId) != null) {
            throw new HTTPException.Builder().causedBy("Sign up method")
                    .badRequest("Duplicate user id : " + userId);
        }
        User user = new User(userId, nickname, password, "mock@mock.com");
        Database.addUser(user);
        return "SUCCESS";
    }
}
