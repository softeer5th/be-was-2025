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

public class LogoutHandler implements ApiHandler {
    private static final Logger logger = LoggerFactory.getLogger(LogoutHandler.class);

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return "POST".equalsIgnoreCase(httpRequest.method())
                && "/api/logout".equalsIgnoreCase(httpRequest.path());
    }

    @Override
    public LoadResult handle(HttpRequest httpRequest) {
        try {
            User user = CookieSessionUtil.getUserFromSession(httpRequest.headers());
            if (user == null) {
                return createResponse(false, "LOGOUT-FAIL", "SID 쿠키가 없거나 세션이 유효하지 않습니다.");
            }

            String sid = CookieSessionUtil.extractSid(httpRequest.headers().get("Cookie"));
            webserver.SessionManager.invalidate(sid);

            logger.debug("[로그아웃 성공] 세션 만료 처리완료. sid={}", sid);
            return createResponse(true, "LOGOUT-SUCCESS", "로그아웃 성공");

        } catch (Exception e) {
            logger.error("로그아웃 처리 중 예외 발생:", e);
            return createResponse(false, "LOGOUT-ERROR", "로그아웃 처리 중 예외가 발생했습니다.");
        }
    }

    private LoadResult createResponse(Boolean isSuccess, String code, String message) {
        CommonResponse response = new CommonResponse(isSuccess, code, message, null);
        String json = JsonUtil.toJson(response);

        return new LoadResult(
                json.getBytes(StandardCharsets.UTF_8),
                "/api/logout",
                "application/json",
                null
        );
    }
}