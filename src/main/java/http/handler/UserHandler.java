package http.handler;

import db.Database;
import http.enums.ErrorMessage;
import http.enums.HttpMethod;
import http.enums.HttpResponseStatus;
import http.request.HttpRequest;
import http.request.HttpRequestParser;
import http.request.TargetInfo;
import http.response.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.JwtUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserHandler implements Handler {
    private static final String REDIRECT_MAIN_HTML = "/index.html";

    private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);

    private static final UserHandler instance = new UserHandler();

    private UserHandler() {}

    public static UserHandler getInstance() {
        return instance;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        TargetInfo target = request.getTarget();
        String path = target.getPath();
        HttpResponse.Builder builder = new HttpResponse.Builder();

        if (request.getMethod() == HttpMethod.POST && path.equals("/user/create")) {
            return handleUserCreate(request, builder);
        } else if (request.getMethod() == HttpMethod.POST && path.equals("/user/login")) {
            return handleUserLogin(request, builder);
        } else {
            builder.errorResponse(HttpResponseStatus.NOT_FOUND, ErrorMessage.NOT_FOUND_PATH_AND_FILE);
        }
        return builder.build();
    }

    private HttpResponse handleUserCreate(HttpRequest request, HttpResponse.Builder builder) throws IOException {
        Map<String, Object> params = HttpRequestParser.parseRequestBody(request.getBody());

        Optional<String> userId = getParam(params, "userId").map(Object::toString);
        Optional<String> name = getParam(params, "name").map(Object::toString);
        Optional<String> password = getParam(params, "password").map(Object::toString);
        Optional<String> email = getParam(params, "email").map(Object::toString);

        if (userId.isEmpty() || name.isEmpty() || password.isEmpty() || email.isEmpty()) {
            return builder
                    .errorResponse(HttpResponseStatus.BAD_REQUEST, ErrorMessage.INVALID_PARAMETER)
                    .build();
        } else if (Database.findUserById(userId.get()) != null) {
            return builder
                    .errorResponse(HttpResponseStatus.BAD_REQUEST, ErrorMessage.USER_ALREADY_EXISTS)
                    .build();
        }

        Database.addUser(new User(userId.get(), name.get(), password.get(), email.get()));

        logger.debug("Add User Complete: {} {} {} {}", userId.get(), name.get(), password.get(), email.get());
        return builder
                .redirectResponse(HttpResponseStatus.FOUND, REDIRECT_MAIN_HTML)
                .build();
    }

    private HttpResponse handleUserLogin(HttpRequest request, HttpResponse.Builder builder) throws IOException {
        Map<String, Object> params = HttpRequestParser.parseRequestBody(request.getBody());

        Optional<String> userId = getParam(params, "userId").map(Object::toString);
        Optional<String> password = getParam(params, "password").map(Object::toString);
        User user;

        if (userId.isEmpty() || password.isEmpty()) {
            return builder
                    .errorResponse(HttpResponseStatus.BAD_REQUEST, ErrorMessage.INVALID_PARAMETER)
                    .build();
        } else if ((user = Database.findUserById(userId.get())) == null) {
            return builder
                    .errorResponse(HttpResponseStatus.UNAUTHORIZED, ErrorMessage.INVALID_ID_PASSWORD)
                    .build();
        }

        logger.debug("User Login: {}, {}", userId.get(), password.get());

        Map<String, String> cookie = new HashMap<>();
        String sid = JwtUtil.generateToken(user);
        cookie.put("sid", sid);
        cookie.put("Path", "/");

        return builder
                .redirectResponse(HttpResponseStatus.FOUND, REDIRECT_MAIN_HTML)
                .setCookie(cookie)
                .build();
    }

    private Optional<Object> getParam(Map<String, Object> params, String key) {
        return Optional.ofNullable(params.get(key));
    }
}
