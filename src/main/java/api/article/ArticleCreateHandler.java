package api.article;

import api.ApiHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import db.Database;
import global.model.CommonResponse;
import global.model.HttpRequest;
import global.model.LoadResult;
import global.util.CookieSessionUtil;
import global.util.JsonUtil;
import model.Article;
import model.User;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

public class ArticleCreateHandler implements ApiHandler {

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return "POST".equalsIgnoreCase(httpRequest.method())
                && "/api/article".equalsIgnoreCase(httpRequest.path());
    }

    @Override
    public LoadResult handle(HttpRequest httpRequest) {
        try {
            User currentUser = CookieSessionUtil.getUserFromSession(httpRequest.headers());
            if (currentUser == null) {
                return createErrorResponse("UNAUTHORIZED", "로그인되지 않은 사용자입니다.");
            }
            Map<String, String> requestData = JsonUtil.fromJson(httpRequest.body(), new TypeReference<>() {});
            String content = requestData.get("content");
            String base64Image = requestData.get("base64Image");

            byte[] imageBytes = null;
            if (base64Image != null && !base64Image.isBlank()) {
                imageBytes = Base64.getDecoder().decode(base64Image);
            }

            Article article = new Article(null, currentUser.getUserId(), content, imageBytes);
            Database.addArticle(article);

            CommonResponse response = new CommonResponse(true, null, null, null);
            String json = JsonUtil.toJson(response);
            return new LoadResult(
                    json.getBytes(StandardCharsets.UTF_8),
                    "/api/article",
                    "application/json",
                    null
            );

        } catch (Exception e) {
            return createErrorResponse("SERVER-ERROR", "게시글 작성 중 오류가 발생했습니다.");
        }
    }

    private LoadResult createErrorResponse(String code, String message) {
        CommonResponse errorResponse = new CommonResponse(false, code, message, null);
        String json = JsonUtil.toJson(errorResponse);
        return new LoadResult(
                json.getBytes(StandardCharsets.UTF_8),
                "/api/article",
                "application/json",
                null
        );
    }
}