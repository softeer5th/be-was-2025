package http;

import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class UserHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);

    public UserHandler() {
    }

    public void createUser(HttpRequest request, HttpResponse response) {
        String redirectPath = "/registration";
        String body = request.getBody();
        Map<String, String> data = parseBody(body);
        String userId = data.get("userId");
        String username = data.get("username");
        String password = data.get("password");
        if (userId == null || username == null || password == null || Database.findUserById(userId) != null) {
            response.writeStatusLine(HttpStatus.SEE_OTHER);
            response.writeHeader(HttpHeader.LOCATION.value(), redirectPath);
            response.send();
            return;
        }
        User user = new User(userId, username, password, null);
        redirectPath = "/";
        Database.addUser(user);
        response.writeStatusLine(HttpStatus.SEE_OTHER);
        response.writeHeader(HttpHeader.LOCATION.value(), redirectPath);
        response.send();
    }

    private Map<String, String> parseBody(String body) {
        Map<String, String> map = new HashMap<>();
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
