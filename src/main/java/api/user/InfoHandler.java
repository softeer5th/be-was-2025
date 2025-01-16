package api.user;

import api.ApiHandler;
import db.Database;
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

public class InfoHandler implements ApiHandler {
    private static final Logger logger = LoggerFactory.getLogger(InfoHandler.class);

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return "GET".equalsIgnoreCase(httpRequest.method())
                && "/api/user/me".equalsIgnoreCase(httpRequest.path());
    }

    @Override
    public LoadResult handle(HttpRequest httpRequest) {
        try {
            Map<String, String> headers = httpRequest.headers();
            if (headers == null) {
                return createErrorResponse("NO-HEADER", "헤더가 없습니다.");
            }
            String cookieHeader = headers.get("Cookie");
            if (cookieHeader == null) {
                return createErrorResponse("NO-COOKIE", "쿠키가 없습니다.");
            }
            String sid = extractSid(cookieHeader);

            User user = SessionManager.getUser(sid);
            if (user == null) {
                return createErrorResponse("UNAUTHORIZED", "로그인되지 않은 사용자입니다.");
            }

            CommonResponse response = new CommonResponse(
                    true,
                    null,
                    null,
                    Map.of("name", user.getName(), "email", user.getEmail())
            );
            String json = toJson(response);

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

    private String extractSid(String cookieHeader) {
        String[] cookiePairs = cookieHeader.split(";");
        for (String pair : cookiePairs) {
            String[] kv = pair.trim().split("=", 2);
            if (kv.length == 2 && "SID".equals(kv[0])) {
                return kv[1];
            }
        }
        return null;
    }

    private LoadResult createErrorResponse(String code, String message) {
        CommonResponse errorResponse = new CommonResponse(false, code, message, null);
        String json = toJson(errorResponse);

        return new LoadResult(
                json.getBytes(StandardCharsets.UTF_8),
                "/api/user/me",
                "application/json",
                null
        );
    }
}