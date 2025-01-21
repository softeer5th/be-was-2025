package api.article;

import api.ApiHandler;
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

public class ArticleReadHandler implements ApiHandler {

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return "GET".equalsIgnoreCase(httpRequest.method())
                && "/api/article".equalsIgnoreCase(httpRequest.path());
    }

    @Override
    public LoadResult handle(HttpRequest httpRequest) {
        try {
            User currentUser = CookieSessionUtil.getUserFromSession(httpRequest.headers());
            if (currentUser == null) {
                return createErrorResponse("UNAUTHORIZED", "로그인되지 않은 사용자입니다.");
            }

            Article latest = Database.findLatestArticleByUserId(currentUser.getUserId());
            if (latest == null) {
                return createErrorResponse("NOT_FOUND", "게시글이 없습니다.");
            }

            String base64Image = null;
            if (latest.getImage() != null && latest.getImage().length > 0) {
                base64Image = Base64.getEncoder().encodeToString(latest.getImage());
            }

            var data = new java.util.HashMap<String, Object>();
            data.put("content", latest.getContent());
            data.put("base64Image", base64Image);

            CommonResponse response = new CommonResponse(true, null, null, data);
            String json = JsonUtil.toJson(response);
            return new LoadResult(
                    json.getBytes(StandardCharsets.UTF_8),
                    "/api/article",
                    "application/json",
                    null
            );
        } catch (Exception e) {
            return createErrorResponse("SERVER-ERROR", "게시글 조회 중 오류가 발생했습니다.");
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