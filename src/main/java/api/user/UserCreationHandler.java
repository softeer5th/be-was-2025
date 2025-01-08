package api.user;

import api.ApiHandler;
import db.Database;
import model.User;
import model.RequestData;
import webserver.load.LoadResult;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class UserCreationHandler implements ApiHandler {
    @Override
    public boolean canHandle(RequestData requestData) {
        if (!"GET".equalsIgnoreCase(requestData.method())) return false;
        return requestData.path().startsWith("/create");
    }

    @Override
    public LoadResult handle(RequestData requestData) throws IOException {
        String path = requestData.path();
        String[] splitQuestion = path.split("\\?", 2);
        if (splitQuestion.length < 2) return null;

        String queryString = splitQuestion[1];
        User user = parseToUser(queryString);
        if (user == null) return null;

        Database.addUser(user);
        String redirectionHtml = "<meta http-equiv='refresh' content='0;url=/index.html' />";
        return new LoadResult(redirectionHtml.getBytes(StandardCharsets.UTF_8), "/create");
    }

    private User parseToUser(String queryString) throws IOException {
        String[] params = queryString.split("&");
        String userId = null;
        String password = null;
        String name = null;

        for (String param : params) {
            String[] kv = param.split("=", 2);
            if (kv.length < 2) continue;

            String key = kv[0];
            String value = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);

            if ("userId".equals(key)) {
                userId = value;
                continue;
            }
            if ("password".equals(key)) {
                password = value;
                continue;
            }
            if ("name".equals(key)) {
                name = value;
            }
        }

        if (userId == null || password == null || name == null) {
            return null;
        }

        return User.of(userId, password, name);
    }
}