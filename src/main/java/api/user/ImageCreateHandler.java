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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

public class ImageCreateHandler implements ApiHandler {
    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return "POST".equalsIgnoreCase(httpRequest.method())
                && "/api/user/image".equalsIgnoreCase(httpRequest.path());
    }

    @Override
    public LoadResult handle(HttpRequest httpRequest) {
        try {
            User currentUser = CookieSessionUtil.getUserFromSession(httpRequest.headers());
            if (currentUser == null) {
                return createErrorResponse("UNAUTHORIZED", "로그인되지 않은 사용자입니다.");
            }
            Map<String, String> requestData = JsonUtil.fromJson(httpRequest.body(), new TypeReference<>() {});
            String base64Image = requestData.get("base64Image");
            if (base64Image == null || base64Image.isBlank()) {
                return createErrorResponse("INVALID-IMAGE", "이미지가 전송되지 않았습니다.");
            }
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            currentUser.setProfileImage(imageBytes);
            Database.updateUser(currentUser);

            CommonResponse response = new CommonResponse(true, null, null, null);
            String json = JsonUtil.toJson(response);
            return new LoadResult(json.getBytes(StandardCharsets.UTF_8), "/api/user/image", "application/json", null);
        } catch (Exception e) {
            return createErrorResponse("SERVER-ERROR", "이미지 업로드 중 오류가 발생했습니다.");
        }
    }

    private LoadResult createErrorResponse(String code, String message) {
        CommonResponse errorResponse = new CommonResponse(false, code, message, null);
        String json = JsonUtil.toJson(errorResponse);
        return new LoadResult(json.getBytes(StandardCharsets.UTF_8), "/api/user/image", "application/json", null);
    }
}