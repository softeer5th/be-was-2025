package handler;

import db.Database;
import db.SessionStore;
import http.constant.HttpHeader;
import http.HttpRequest;
import http.HttpResponse;
import model.Session;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Cookie;
import util.RequestParser;
import util.SessionUtils;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class UserHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);

    public UserHandler() {
    }

    public void createUser(HttpRequest request, HttpResponse response) throws UnsupportedEncodingException {
        Map<String, String> data = RequestParser.parseBody(new String(request.getBody()));
        String userId = data.get("userId");
        String username = data.get("username");
        String password = data.get("password");

        if (userId == null || username == null || password == null) {
            response.redirect("/registration");
            return;
        }

        Database.findUserById(userId) .ifPresentOrElse(user -> {
                response.redirect("/registration");
            }, () -> {
                User user = new User(userId, password, username, null);
                Database.addUser(user);

                response.redirect("/");
            }
        );

    }

    public void loginUser(HttpRequest request, HttpResponse response) throws UnsupportedEncodingException {
        Map<String, String> data = RequestParser.parseBody(new String(request.getBody()));
        String userId = data.get("userId");
        String password = data.get("password");
        if (userId == null || password == null) {
            response.redirect("/login/failed.html");
            return;
        }

        Database.findUserById(userId).ifPresentOrElse(user -> {
                if (!user.getPassword().equals(password)) {
                    response.redirect("/login/failed.html");
                    return;
                }

                Cookie cookie = new Cookie();
                cookie.setPath("/");
                cookie.setMaxAge(180);
                Session session = new Session(cookie.getValue(), user.getUserId());

                SessionStore.addSession(cookie.getValue(), session);

                response.writeHeader(HttpHeader.SET_COOKIE, cookie.createCookieString());
                response.redirect("/main");
            }, () -> {
                response.redirect("/login/failed.html");
            }
        );
    }

    public void logoutUser(HttpRequest request, HttpResponse response) {
        Session session = SessionUtils.findSession(request);

        SessionStore.deleteBySessionId(session.sessionId());

        response.redirect("/");
    }
}
