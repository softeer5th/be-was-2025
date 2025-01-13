package http;

import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class UserHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);

    public UserHandler() {
    }

    public void createUser(HttpRequest request, HttpResponse response) {
        String redirectPath = "/registration";
        Map<String, String> queries = request.getQueries();
        String userId = queries.get("userId");
        String username = queries.get("username");
        String password = queries.get("password");
        if (userId == null || username == null || password == null || Database.findUserById(userId) != null) {
            response.writeStatusLine(HttpStatus.SEE_OTHER);
            response.writeHeader("Location", redirectPath);
            response.send();
            return;
        }
        User user = new User(queries.get("userId"), queries.get("username"), queries.get("password"), null);
        redirectPath = "/login";
        Database.addUser(user);
        response.writeStatusLine(HttpStatus.SEE_OTHER);
        response.writeHeader("Location", redirectPath);
        response.send();
    }
}
