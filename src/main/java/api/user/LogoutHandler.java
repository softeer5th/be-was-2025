package api.user;

import api.ApiHandler;
import global.model.CommonResponse;
import global.model.HttpRequest;
import global.model.LoadResult;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.SessionManager;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static global.util.JsonUtil.toJson;

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
            String sid = extractSid(httpRequest);
            if (sid == null) {
                logger.debug("[로그아웃 실패] SID 쿠키 없음.");
                return createResponse(false, "LOGOUT-FAIL", "SID 쿠키가 없습니다.");
            }

            User user = SessionManager.getUser(sid);
            if (user == null) {
                logger.debug("[로그아웃 실패] 이미 만료된 세션이거나 존재하지 않는 세션.");
                return createResponse(false, "LOGOUT-FAIL", "유효하지 않은 세션입니다.");
            }

            SessionManager.invalidate(sid);
            logger.debug("[로그아웃 성공] 세션 만료 처리완료. sid={}", sid);

            return createResponse(true, "LOGOUT-SUCCESS", "로그아웃 성공");

        } catch (Exception e) {
            logger.error("로그아웃 처리 중 예외 발생:", e);
            return createResponse(false, "LOGOUT-ERROR", "로그아웃 처리 중 예외가 발생했습니다.");
        }
    }

    private String extractSid(HttpRequest httpRequest) {
        Map<String, String> headers = httpRequest.headers();
        if (headers == null) return null;

        String cookieHeader = headers.get("Cookie");
        if (cookieHeader == null) return null;

        String[] cookiePairs = cookieHeader.split(";");
        for (String pair : cookiePairs) {
            String[] kv = pair.trim().split("=", 2);
            if (kv.length == 2 && "SID".equals(kv[0])) {
                return kv[1];
            }
        }
        return null;
    }

    private LoadResult createResponse(Boolean isSuccess, String code, String message) {
        CommonResponse response = new CommonResponse(isSuccess, code, message, null);
        String json = toJson(response);
        return new LoadResult(json.getBytes(StandardCharsets.UTF_8),
                "/api/logout",
                "application/json",
                null);
    }
}