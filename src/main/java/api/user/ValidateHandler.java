package api.user;

import api.ApiHandler;
import global.model.CommonResponse;
import global.model.HttpRequest;
import global.model.LoadResult;
import global.util.CookieSessionUtil;
import global.util.JsonUtil;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class ValidateHandler implements ApiHandler {
    private static final Logger logger = LoggerFactory.getLogger(ValidateHandler.class);

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return "GET".equalsIgnoreCase(httpRequest.method())
                && "/api/user/validate".equalsIgnoreCase(httpRequest.path());
    }

    @Override
    public LoadResult handle(HttpRequest httpRequest) {
        try {
            User user = CookieSessionUtil.getUserFromSession(httpRequest.headers());
            if (user == null) {
                return createErrorResponse("UNAUTHORIZED", "로그인되지 않은 사용자입니다.");
            }

            CommonResponse response = new CommonResponse(true, null, null, user.getName());
            String json = JsonUtil.toJson(response);
            logger.debug("내 정보 반환 JSON: {}", json);

            return new LoadResult(
                    json.getBytes(StandardCharsets.UTF_8),
                    "/api/user/me",
                    "application/json",
                    null
            );

        } catch (Exception e) {
            logger.error("사용자 정보 조회 중 에러:", e);
            return createErrorResponse("SERVER-ERROR", "서버 에러가 발생했습니다.");
        }
    }

    private LoadResult createErrorResponse(String code, String message) {
        CommonResponse errorResponse = new CommonResponse(false, code, message, null);
        String json = JsonUtil.toJson(errorResponse);

        return new LoadResult(
                json.getBytes(StandardCharsets.UTF_8),
                "/api/user/me",
                "application/json",
                null
        );
    }
}