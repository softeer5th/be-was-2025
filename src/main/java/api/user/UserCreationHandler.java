package api.user;

import api.ApiHandler;
import db.Database;
import model.User;
import model.RequestData;
import webserver.load.LoadResult;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class UserCreationHandler implements ApiHandler {
    @Override
    public boolean canHandle(RequestData requestData) {
        if (!"GET".equalsIgnoreCase(requestData.method())) return false;
        return requestData.path().startsWith("/create");
    }

    @Override
    public LoadResult handle(RequestData requestData) {
        String path = requestData.path();
        String[] splitQuestion = path.split("\\?", 2);
        if (splitQuestion.length < 2) return null;

        String queryString = splitQuestion[1];
        User user = createUserFromQuery(queryString);
        if (user == null) return null;

        Database.addUser(user);
        String redirectionHtml = "<meta http-equiv='refresh' content='0;url=/index.html' />";
        return new LoadResult(redirectionHtml.getBytes(StandardCharsets.UTF_8), "/create");
    }

    private User createUserFromQuery(String queryString) {
        Map<String, String> parameters = parseQueryString(queryString);

        String userId = parameters.get("userId");
        String password = parameters.get("password");
        String name = parameters.get("name");

        if (userId == null || password == null || name == null) {
            return null;
        }

        return User.of(userId, password, name);
    }

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> parameters = new HashMap<>();
        String[] params = queryString.split("&");

        for (String param : params) {
            String[] kv = param.split("=", 2);
            if (kv.length < 2) continue;

            String key = kv[0];
            String value = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
            parameters.put(key, value);
        }

        return parameters;
    }
}