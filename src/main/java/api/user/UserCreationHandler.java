package api.user;

import api.ApiHandler;
import global.exception.ErrorCode;
import global.exception.UserCreationException;
import db.Database;
import global.model.CommonResponse;
import model.User;
import global.model.RequestData;
import global.model.LoadResult;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static global.util.JsonUtil.toJson;

public class UserCreationHandler implements ApiHandler {

    @Override
    public boolean canHandle(RequestData requestData) {
        if (!"GET".equalsIgnoreCase(requestData.method())) {
            throw new UserCreationException(ErrorCode.INVALID_USER_INPUT);
        }
        return requestData.path().startsWith("/api/create");
    }

    @Override
    public LoadResult handle(RequestData requestData) {
        String queryString = extractQueryString(requestData.path());
        User user = createUserFromQuery(queryString);
        try {
            validateUser(user);
        } catch (UserCreationException e) {
            return handleSignupFailure(e);
        }
        Database.addUser(user);
        return createSuccessResponse();
    }

    private String extractQueryString(String path) {
        String[] splitQuestion = path.split("\\?", 2);
        if (splitQuestion.length < 2) {
            throw new UserCreationException(ErrorCode.INVALID_USER_INPUT);
        }
        return splitQuestion[1];
    }

    private User createUserFromQuery(String queryString) {
        Map<String, String> parameters = parseQueryString(queryString);

        String userId = parameters.get("userId");
        String password = parameters.get("password");
        String name = parameters.get("name");

        if (userId == null || password == null || name == null) {
            throw new UserCreationException(ErrorCode.INVALID_USER_INPUT);
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

    private void validateUser(User user) {
        if (Database.findUserById(user.getUserId()) != null) {
            throw new UserCreationException(ErrorCode.USER_ALREADY_EXISTS);
        }

        boolean nameExists = Database.findAll().stream()
                .anyMatch(existingUser -> user.getName().equals(existingUser.getName()));
        if (nameExists) {
            throw new UserCreationException(ErrorCode.DUPLICATED_NAME);
        }
    }

    private LoadResult handleSignupFailure(UserCreationException e) {
        CommonResponse commonResponse;

        if (e.getErrorCode() == ErrorCode.USER_ALREADY_EXISTS) {
            commonResponse = new CommonResponse(
                    false,
                    "SIGNUP-01",
                    e.getErrorCode().getMessage(),
                    null
            );
        } else if (e.getErrorCode() == ErrorCode.DUPLICATED_NAME) {
            commonResponse = new CommonResponse(
                    false,
                    "SIGNUP-02",
                    e.getErrorCode().getMessage(),
                    null
            );
        } else {
            commonResponse = new CommonResponse(
                    false,
                    "UNKNOWN_ERROR",
                    e.getErrorCode().getMessage(),
                    null
            );
        }

        String json = toJson(commonResponse);
        return new LoadResult(json.getBytes(StandardCharsets.UTF_8), "/api/create", "application/json");
    }

    private LoadResult createSuccessResponse() {
        CommonResponse commonResponse = new CommonResponse(true, null, null, null);
        String json = toJson(commonResponse);
        return new LoadResult(json.getBytes(StandardCharsets.UTF_8), "/api/create", "application/json");
    }
}