package http.handler;

import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import http.response.ContentType;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.enums.HttpResponseStatus;
import http.request.TargetInfo;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class UserHandler implements Handler {
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

        if (path.equals("/user/create")) {
            handleUserCreate(target.getParams(), response);
        } else {
            response.sendErrorResponse(HttpResponseStatus.NOT_FOUND);
        }
    }

    private void handleUserCreate(Map<String, String> params, HttpResponse response) throws IOException {
        Optional<String> userId = getParam(params, "userId");
        Optional<String> name = getParam(params, "name");
        Optional<String> password = getParam(params, "password");
        Optional<String> email = getParam(params, "email");

        if (userId.isEmpty() || name.isEmpty() || password.isEmpty() || email.isEmpty()) {
            response.sendErrorResponse(HttpResponseStatus.BAD_REQUEST);
            return;
        }

        Database.addUser(new User(userId.get(), name.get(), password.get(), email.get()));

        logger.debug("Add User Complete: {} {} {} {}", userId.get(), name.get(), password.get(), email.get());
        String body = String.format("<h1>%s님 회원 가입 완료</h1>", name.get());
        response.sendSuccessResponse(HttpResponseStatus.CREATED, ContentType.HTML.getMimeType(), body);
    }

    private Optional<String> getParam(Map<String, String> params, String key) {
        return Optional.ofNullable(params.get(key));
    }
}
