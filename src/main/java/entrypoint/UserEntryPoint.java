package entrypoint;

import db.Database;
import model.User;
import webserver.annotation.Body;
import webserver.annotation.Cookie;
import webserver.annotation.RequestMapping;
import webserver.enumeration.HTTPContentType;
import webserver.enumeration.HTTPMethod;
import webserver.exception.HTTPException;
import webserver.message.record.ResponseData;
import webserver.message.record.SetCookieRecord;
import webserver.session.SessionStorage;
import webserver.writer.html.template.IndexPageWriter;

import java.util.Map;
import java.util.Optional;
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
        SetCookieRecord loginCookie = new SetCookieRecord.Builder("SID", sessionId)
                .path("/")
                .build();
        if (SessionStorage.getStorage(sessionId) == null) {
            Map<String, String> newSession = SessionStorage.setSession(sessionId);
            newSession.put("userId", userId);
        }
        return new ResponseData.ResponseDataBuilder<String>()
                .setCookies(loginCookie)
                .redirect("/index.html");
    }

    @RequestMapping(path = "/index.html", method = HTTPMethod.GET)
    public ResponseData<String> homePage(@Cookie(name="SID", authenticated = true) String sid) {
        Map<String, String> storage = SessionStorage.getStorage(sid);
        Optional<String> userName = Optional.empty();
        if (storage != null) {
            userName = Optional.ofNullable(storage.get("userId"));
        }
        String body = IndexPageWriter.write(userName);
        return new ResponseData.ResponseDataBuilder<String>()
                .contentType(HTTPContentType.TEXT_HTML)
                .ok(body);
    }
}
