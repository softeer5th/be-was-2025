package http;

import db.Database;
import db.SessionStore;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Cookie;

import java.util.HashMap;
import java.util.Map;

public class UserHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);

    public UserHandler() {
    }

    public void createUser(HttpRequest request, HttpResponse response) {
        Map<String, String> data = parseBody(request);
        String userId = data.get("userId");
        String username = data.get("username");
        String password = data.get("password");

        if (userId == null || username == null || password == null) {
            response.redirect("/registration");
            return;
        }

        Database.findUserById(userId) .ifPresentOrElse(
            user -> {
                response.redirect("/registration");
            }, () -> {
                User user = new User(userId, username, password, null);
                Database.addUser(user);

                response.redirect("/");
            }
        );

    }

    public void loginUser(HttpRequest request, HttpResponse response) {
        Map<String, String> data = parseBody(request);
        String userId = data.get("userId");
        String password = data.get("password");
        if (userId == null | password == null) {
            response.redirect("/login/failed.html");
            return;
        }

        Database.findUserById(userId).ifPresentOrElse(
                user -> {
                if (!user.getPassword().equals(password)) {
                    response.redirect("/login/failed.html");
                    return;
                }

                Cookie cookie = new Cookie("sid");
                cookie.setPath("/");
                cookie.setMaxAge(180);
                SessionStore.addSession(cookie.getValue(), user);

                response.writeHeader(HttpHeader.SET_COOKIE, cookie.createCookieString());
                response.redirect("/");
            }, () -> {
                response.redirect("/login/failed.html");
            }
        );
    }

    private Map<String, String> parseBody(HttpRequest request) {
        Map<String, String> map = new HashMap<>();
        String body = request.getBody();
        String[] tokens = body.split("&");
        for(String token: tokens) {
            String[] items = token.split("=");
            String key = items[0].trim();
            String value = items.length > 1 ? items[1].trim() : null;
            map.put(key, value);
        }
        return map;
    }
}
