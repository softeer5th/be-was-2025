package api.user;

import api.ApiHandler;
import db.Database;
import global.model.CommonResponse;
import global.model.HttpRequest;
import global.model.LoadResult;
import global.util.JsonUtil;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.SessionManager;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static global.util.JsonUtil.toJson;

public class LoginHandler implements ApiHandler {
    private static final Logger logger = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return "POST".equalsIgnoreCase(httpRequest.method())
                && "/api/login".equalsIgnoreCase(httpRequest.path());
    }

    @Override
    public LoadResult handle(HttpRequest httpRequest) {
        try {
            Map<String, String> credentials = extractCredentials(httpRequest.body());
            String userId = credentials.get("userId");
            String password = credentials.get("password");

            User user = validateUser(userId, password);

            return createSuccessResponse(user);
        } catch (IllegalArgumentException e) {
            logger.debug("[로그인 실패] 요청 데이터 오류: {}", e.getMessage());
            return createErrorResponse("LOGIN-FAIL", e.getMessage());
        } catch (Exception e) {
            logger.error("로그인 처리 중 에러:", e);
            return createErrorResponse("LOGIN-ERROR", "로그인 중 예외가 발생했습니다.");
        }
    }

    private Map<String, String> extractCredentials(String body) {
        Map<String, String> requestData = JsonUtil.fromJson(body);

        String userId = requestData.get("userId");
        String password = requestData.get("password");

        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("아이디가 입력되지 않았습니다.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호가 입력되지 않았습니다.");
        }

        return requestData;
    }

    /**
     * 사용자 검증
     */
    private User validateUser(String userId, String password) {
        User user = Database.findUserById(userId);

        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다.");
        }

        return user;
    }

    private LoadResult createSuccessResponse(User user) {
        String sessionId = SessionManager.createSession(user);
        logger.debug("[로그인 성공] - 세션 생성 sessionId: {}", sessionId);

        CommonResponse successResponse = new CommonResponse(
                true,
                null,
                null,
                null
        );
        String json = toJson(successResponse);
        String cookieValue = "SID=" + sessionId + "; Path=/";

        return new LoadResult(
                json.getBytes(StandardCharsets.UTF_8),
                "/api/login",
                "application/json",
                cookieValue
        );
    }

    private LoadResult createErrorResponse(String errorCode, String errorMessage) {
        CommonResponse errorResponse = new CommonResponse(
                false,
                errorCode,
                errorMessage,
                null
        );
        String json = toJson(errorResponse);

        return new LoadResult(
                json.getBytes(StandardCharsets.UTF_8),
                "/api/login",
                "application/json",
                null
        );
    }
}