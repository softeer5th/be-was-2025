package http;

import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.util.Map;

public class UserHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);

    public UserHandler() {
    }

    public void createUser(HttpRequest request, DataOutputStream dos) {
        String redirectPath = "/registration";
        Map<String, String> queries = request.getQueries();
        String userId = queries.get("userId");
        String username = queries.get("username");
        String password = queries.get("password");
        if (userId == null || username == null || password == null || Database.findUserById(userId) != null) {
            HttpResponseHandler.redirect(dos, redirectPath);
            return;
        }
        User user = new User(queries.get("userId"), queries.get("username"), queries.get("password"), null);
        redirectPath = "/login";
        Database.addUser(user);
        HttpResponseHandler.redirect(dos, redirectPath);
    }
}
