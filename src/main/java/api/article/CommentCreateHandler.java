package api.article;

import api.ApiHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import db.Database;
import global.model.CommonResponse;
import global.model.HttpRequest;
import global.model.LoadResult;
import global.util.CookieSessionUtil;
import global.util.JsonUtil;
import model.Comment;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CommentCreateHandler implements ApiHandler {

    private static final Logger logger = LoggerFactory.getLogger(CommentCreateHandler.class);

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return "POST".equalsIgnoreCase(httpRequest.method())
                && "/api/comment".equalsIgnoreCase(httpRequest.path());
    }

    @Override
    public LoadResult handle(HttpRequest httpRequest) {
        try {
            User currentUser = CookieSessionUtil.getUserFromSession(httpRequest.headers());
            if (currentUser == null) {
                logger.warn("댓글 작성 요청 시 비로그인 사용자입니다.");
                return createErrorResponse("UNAUTHORIZED", "로그인되지 않은 사용자입니다.");
            }

            Map<String, String> requestData = JsonUtil.fromJson(httpRequest.body(), new TypeReference<>() {});
            String content = requestData.get("content");
            String articleIdStr = requestData.get("articleId");

            if (content == null || content.isBlank()) {
                logger.info("댓글 내용이 비어있습니다.");
                return createErrorResponse("INVALID-CONTENT", "댓글 내용이 비어있습니다.");
            }
            if (articleIdStr == null || articleIdStr.isBlank()) {
                logger.info("게시글 식별자 값이 없습니다.");
                return createErrorResponse("INVALID-ARTICLE", "게시글 식별자가 없습니다.");
            }

            long articleId = Long.parseLong(articleIdStr);

            Comment comment = new Comment(null, articleId, currentUser.getUserId(), content);
            Database.addComment(comment);

            logger.info("댓글이 정상적으로 작성되었습니다. (작성자: {}, 게시글 ID: {})", currentUser.getUserId(), articleId);

            CommonResponse response = new CommonResponse(true, null, null, null);
            String json = JsonUtil.toJson(response);
            return new LoadResult(
                    json.getBytes(StandardCharsets.UTF_8),
                    "/api/comment",
                    "application/json",
                    null
            );
        } catch (Exception e) {
            logger.error("댓글 작성 중 오류가 발생했습니다.", e);
            return createErrorResponse("SERVER-ERROR", "댓글 작성 중 오류가 발생했습니다.");
        }
    }

    private LoadResult createErrorResponse(String code, String message) {
        CommonResponse errorResponse = new CommonResponse(false, code, message, null);
        String json = JsonUtil.toJson(errorResponse);
        return new LoadResult(
                json.getBytes(StandardCharsets.UTF_8),
                "/api/comment",
                "application/json",
                null
        );
    }
}