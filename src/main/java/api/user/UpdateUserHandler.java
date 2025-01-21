package api.user;

import api.ApiHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import db.Database;
import global.model.CommonResponse;
import global.model.HttpRequest;
import global.model.LoadResult;
import global.util.CookieSessionUtil;
import global.util.JsonUtil;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class UpdateUserHandler implements ApiHandler {
    private static final Logger logger = LoggerFactory.getLogger(UpdateUserHandler.class);

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return "PUT".equalsIgnoreCase(httpRequest.method())
                && "/api/user".equalsIgnoreCase(httpRequest.path());
    }

    @Override
    public LoadResult handle(HttpRequest httpRequest) {
        try {
            User currentUser = validateUserSession(httpRequest);
            Map<String, String> requestData = parseRequestData(httpRequest);

            String newName = requestData.get("nickname");
            String newPassword = requestData.get("password");

            validateNickname(currentUser, newName);
            updateUserDetails(currentUser, newName, newPassword);

            return createSuccessResult();
        } catch (IllegalArgumentException e) {
            logger.error("사용자 정보 수정 중 요청 오류: {}", e.getMessage());
            return createErrorResult("INVALID-INPUT", e.getMessage());
        } catch (Exception e) {
            logger.error("사용자 정보 수정 중 예외 발생:", e);
            return createErrorResult("UPDATE-ERROR", "사용자 정보 수정 중 예외가 발생했습니다.");
        }
    }

    private User validateUserSession(HttpRequest httpRequest) {
        User currentUser = CookieSessionUtil.getUserFromSession(httpRequest.headers());
        if (currentUser == null) {
            throw new IllegalArgumentException("로그인되지 않은 사용자입니다.");
        }
        return currentUser;
    }

    private Map<String, String> parseRequestData(HttpRequest httpRequest) {
        try {
            return JsonUtil.fromJson(httpRequest.body(), new TypeReference<>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("잘못된 요청 데이터 형식입니다.");
        }
    }

    private void validateNickname(User currentUser, String newName) {
        if (!newName.equals(currentUser.getName())) {
            boolean nameExists = Database.findAll().stream()
                    .anyMatch(u -> !u.getUserId().equals(currentUser.getUserId())
                            && u.getName().equals(newName));
            if (nameExists) {
                throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
            }
        }
    }

    private void updateUserDetails(User currentUser, String newName, String newPassword) {
        currentUser.setName(newName);
        if (newPassword != null && !newPassword.isBlank()) {
            currentUser.setPassword(newPassword);
        }
        logger.debug("사용자 정보 수정 완료: userId={}, name={}, password={}",
                currentUser.getUserId(), currentUser.getName(), currentUser.getPassword());
    }

    private LoadResult createSuccessResult() {
        CommonResponse response = new CommonResponse(true, null, null, null);
        String json = JsonUtil.toJson(response);

        return new LoadResult(
                json.getBytes(StandardCharsets.UTF_8),
                "/api/user/update",
                "application/json",
                null
        );
    }

    private LoadResult createErrorResult(String code, String message) {
        CommonResponse errorResponse = new CommonResponse(false, code, message, null);
        String json = JsonUtil.toJson(errorResponse);

        return new LoadResult(
                json.getBytes(StandardCharsets.UTF_8),
                "/api/user/update",
                "application/json",
                null
        );
    }
}