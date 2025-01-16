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
            User currentUser = CookieSessionUtil.getUserFromSession(httpRequest.headers());
            if (currentUser == null) {
                return createErrorResult("UNAUTHORIZED", "로그인되지 않은 사용자입니다.");
            }

            Map<String, String> requestData = JsonUtil.fromJson(httpRequest.body(), new TypeReference<>() {});

            String newName = requestData.get("nickname");
            String newPassword = requestData.get("password");

            if (!newName.equals(currentUser.getName())) {
                boolean nameExists = Database.findAll().stream()
                        .anyMatch(u -> !u.getUserId().equals(currentUser.getUserId())
                                && u.getName().equals(newName));
                if (nameExists) {
                    return createErrorResult("DUPLICATED_NAME", "이미 사용 중인 닉네임입니다.");
                }
            }

            currentUser.setName(newName);

            if (newPassword != null && !newPassword.isBlank()) {
                currentUser.setPassword(newPassword);
            }

            logger.debug("사용자 정보 수정 완료: userId={}, name={}, password={}",
                    currentUser.getUserId(), currentUser.getName(), currentUser.getPassword());

            CommonResponse response = new CommonResponse(true, null, null, null);
            String json = JsonUtil.toJson(response);

            return new LoadResult(
                    json.getBytes(StandardCharsets.UTF_8),
                    "/api/user/update",
                    "application/json",
                    null
            );

        } catch (Exception e) {
            logger.error("사용자 정보 수정 중 에러:", e);
            return createErrorResult("UPDATE-ERROR", "사용자 정보 수정 중 예외가 발생했습니다.");
        }
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