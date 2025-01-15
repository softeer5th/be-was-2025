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

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class UserHandler implements Handler {
    private static final String REDIRECT_MAIN_HTML = "/index.html";

    private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);

    private static UserHandler instance = new UserHandler();

    private UserHandler() {}

    public static UserHandler getInstance() {
        return instance;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        TargetInfo target = request.getTarget();
        String path = target.getPath();

        if (request.getMethod() == HttpMethod.POST && path.equals("/user/create")) {
            handleUserCreate(request, response);
        } else {
            response.sendErrorResponse(HttpResponseStatus.NOT_FOUND, ErrorMessage.NOT_FOUND_PATH_AND_FILE);
        }
    }

    private void handleUserCreate(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, Object> params = HttpRequestParser.parseRequestBody(request.getBody());

        Optional<String> userId = getParam(params, "userId").map(Object::toString);
        Optional<String> name = getParam(params, "name").map(Object::toString);
        Optional<String> password = getParam(params, "password").map(Object::toString);
        Optional<String> email = getParam(params, "email").map(Object::toString);

        if (userId.isEmpty() || name.isEmpty() || password.isEmpty() || email.isEmpty()) {
            response.sendErrorResponse(HttpResponseStatus.BAD_REQUEST, ErrorMessage.INVALID_PARAMETER);
            return;
        } else if (Database.findUserById(userId.get()) != null) {
            response.sendErrorResponse(HttpResponseStatus.BAD_REQUEST, ErrorMessage.USER_ALREADY_EXISTS);
            return;
        }

        Database.addUser(new User(userId.get(), name.get(), password.get(), email.get()));

        logger.debug("Add User Complete: {} {} {} {}", userId.get(), name.get(), password.get(), email.get());
        response.sendRedirectResponse(HttpResponseStatus.FOUND, REDIRECT_MAIN_HTML);
    }

    private Optional<Object> getParam(Map<String, Object> params, String key) {
        return Optional.ofNullable(params.get(key));
    }
}
