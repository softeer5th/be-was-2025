package handler;

import static http.HttpMethod.GET;

import http.HttpRequestInfo;
import http.HttpStatus;
import java.util.HashMap;
import java.util.Map;
import model.User;
import http.HttpResponse;

public class UserRegisterHandler implements Handler {

    private static final String USER_REQUEST_PREFIX = "/users/";

    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        String path = request.getPath().substring(USER_REQUEST_PREFIX.length());
        HttpResponse response = new HttpResponse();

        if (request.getMethod().equals(GET) && path.startsWith("register?")) {
            String query = path.substring("register?".length());
            Map<String, String> queryParams = parseQueryParams(query);

            String userId = queryParams.get("userId");
            String nickname = queryParams.get("nickname");
            String password = queryParams.get("password");
            String email = queryParams.get("email");

            User user = new User(userId, nickname, password, email);
            user.registerUser();

            response.setStatus(HttpStatus.FOUND);
            response.setHeaders("Location", "/index.html");
        }

        return response;
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }

}