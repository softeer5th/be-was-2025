package entrypoint;

import db.Database;
import model.User;
import webserver.annotation.Body;
import webserver.annotation.RequestMapping;
import webserver.enumeration.HTTPMethod;
import webserver.exception.HTTPException;
import webserver.message.record.ResponseData;
import webserver.message.record.SetCookieRecord;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserEntryPoint {
    @RequestMapping(path = "/user/create", method = HTTPMethod.POST)
    public ResponseData<String> signUp(
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
        return ResponseData.redirect("/index.html");
    }

    @RequestMapping(path= "/login", method = HTTPMethod.POST)
    public ResponseData<String> login(
            @Body(key="userId") String userId,
            @Body(key="password") String password
    ) {
        User user = Database.findUserById(userId);
        if (user == null || !user.getPassword().equals(password)) {
            return ResponseData.redirect("/user/login_failed.html");
        }
        String sessionId = UUID.randomUUID().toString();
        SetCookieRecord loginCookie = new SetCookieRecord(
            "SID",
            sessionId,
            LocalDateTime.of(2025,12,31,0,0)
        );
        return new ResponseData.ResponseDataBuilder<String>()
                .setCookies(loginCookie)
                .redirect("/index.html");
    }
}
