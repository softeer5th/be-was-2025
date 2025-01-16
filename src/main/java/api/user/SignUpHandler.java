package api.user;

import api.ApiHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import global.exception.ErrorCode;
import global.exception.UserCreationException;
import db.Database;
import global.model.CommonResponse;
import global.model.HttpRequest;
import global.util.JsonUtil;
import model.User;
import global.model.LoadResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static global.util.JsonUtil.toJson;

public class SignUpHandler implements ApiHandler {
    private static final Logger logger = LoggerFactory.getLogger(SignUpHandler.class);

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return "POST".equalsIgnoreCase(httpRequest.method())
                && httpRequest.path().startsWith("/api/create");
    }

    @Override
    public LoadResult handle(HttpRequest httpRequest) {
        User user;
        try {
            user = createUserFromBody(httpRequest.body());
            validateUser(user);
        } catch (UserCreationException e) {
            return handleSignupFailure(e);
        }
        Database.addUser(user);
        logger.debug("유저가 DB에 추가되었습니다.");
        return createRedirectResponse();
    }

    private User createUserFromBody(String body) {
        Map<String, String> requestData = JsonUtil.fromJson(body, new TypeReference<>() {});

        String userId = requestData.get("userId");
        String password = requestData.get("password");
        String name = requestData.get("name");

        if (userId == null || password == null || name == null) {
            logger.error("회원가입에 필요한 모든 값이 입력되지 않았습니다.");
            throw new UserCreationException(ErrorCode.INVALID_USER_INPUT);
        }

        return new User(userId, password, name,null);
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
        logger.debug("유저 생성 검증에 성공하였습니다.");
    }

    private LoadResult handleSignupFailure(UserCreationException e) {
        CommonResponse commonResponse = new CommonResponse(
                false,
                e.getErrorCode().getCode(),
                e.getErrorCode().getMessage(),
                null
        );

        String json = toJson(commonResponse);
        return new LoadResult(json.getBytes(StandardCharsets.UTF_8), "/api/create", "application/json",null);
    }

    private LoadResult createRedirectResponse() {
        return new LoadResult(null, "/index.html", "redirect",null);
    }
}