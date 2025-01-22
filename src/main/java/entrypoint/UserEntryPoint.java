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
import webserver.reader.StaticFileReader;
import webserver.session.SessionStorage;
import webserver.writer.html.template.IndexPageWriter;
import webserver.writer.html.template.MyPageWriter;

import javax.swing.text.html.Option;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class UserEntryPoint {
    private Database database;

    public UserEntryPoint(Database database) {
        this.database = database;
    }

    @RequestMapping(path = "/user/create", method = HTTPMethod.POST)
    public ResponseData<String> signUp(
            @Body(key="userId") String userId,
            @Body(key="nickname") String nickname,
            @Body(key="password") String password
    ) {
        if (database.findUserById(userId).isPresent()) {
            throw new HTTPException.Builder().causedBy("Sign up method")
                    .badRequest("Duplicate user id : " + userId);
        }
        User user = new User(userId, nickname, password, "mock@mock.com");
        database.addUser(user);
        return ResponseData.redirect("/index.html");
    }

    @RequestMapping(path= "/login", method = HTTPMethod.POST)
    public ResponseData<String> login(
            @Body(key="userId") String userId,
            @Body(key="password") String password
    ) {
        Optional<User> user = database.findUserById(userId);
        if (user.isEmpty() || !user.get().getPassword().equals(password)) {
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

    @RequestMapping(path = "/index", method = HTTPMethod.GET)
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

    @RequestMapping(path = "/", method = HTTPMethod.GET)
    public ResponseData<String> index() {
        return ResponseData.redirect("/index");
    }

    @RequestMapping(path = "/index.html", method = HTTPMethod.GET)
    public ResponseData<String> indexPage() {
        return ResponseData.redirect("/index");
    }

    @RequestMapping(path = "/mypage", method = HTTPMethod.GET)
    public ResponseData<String> myPage(@Cookie(name="SID", required = false) String sid) {
        if (sid == null || SessionStorage.getStorage(sid) == null) {
            return ResponseData.redirect("/login");
        }
        String body = MyPageWriter.write(sid);
        return new ResponseData.ResponseDataBuilder<String>()
                .contentType(HTTPContentType.TEXT_HTML)
                .ok(body);
    }

    @RequestMapping(path = "/mypage.html", method = HTTPMethod.GET)
    public ResponseData<String> myPageHtml(@Cookie(name="SID", required = false) String sid) {
        return ResponseData.redirect("/mypage");
    }

    @RequestMapping(path = "/article", method = HTTPMethod.GET)
    public ResponseData<String> getCreateArticleForm(@Cookie(name = "SID") String sid) {
        if (sid == null || SessionStorage.getStorage(sid) == null) {
            return ResponseData.redirect("/login");
        }
        String page = StaticFileReader.getStaticFile("/article/index.html")
                .orElseThrow(() -> new HTTPException.Builder().notFound("/article"));
        return new ResponseData.ResponseDataBuilder<String>()
                .contentType(HTTPContentType.TEXT_HTML)
                .ok(page);
    }

    @RequestMapping(path="/article/create", method=HTTPMethod.POST)
    public ResponseData<String> createArticle(
        @Cookie(name="SID") String sid,
        @Body(key="title") String title,
        @Body(key="body") String body
    )  {
        Map<String, String> session = SessionStorage.getStorage(sid);
        if (session == null) {
            return ResponseData.redirect("/login");
        }
        this.database.enrollPost(session.get("userId"), title, body);
        return new ResponseData.ResponseDataBuilder<String>()
                .redirect("/index");
    }
}
