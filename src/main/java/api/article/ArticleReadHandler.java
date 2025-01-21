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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

public class ArticleReadHandler implements ApiHandler {
    private static final Logger logger = LoggerFactory.getLogger(ArticleReadHandler.class);


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

            var data = new ConcurrentHashMap<String, Object>();
            data.put("content", latest.getContent());

            String base64Image;
            if (latest.getImage() != null && latest.getImage().length > 0) {
                base64Image = Base64.getEncoder().encodeToString(latest.getImage());
                data.put("base64Image", base64Image);
                logger.debug("이미지 데이터가 Base64로 인코딩되었습니다.");
            }

            CommonResponse response = new CommonResponse(true, null, null, data);
            String json = JsonUtil.toJson(response);
            logger.info("게시글 조회 성공. 사용자 ID: {}, 게시글 내용: {}", currentUser.getUserId(), latest.getContent());
            return new LoadResult(
                    json.getBytes(StandardCharsets.UTF_8),
                    "/api/article",
                    "application/json",
                    null
            );
        } catch (Exception e) {
            logger.error("게시글 조회 중 오류가 발생했습니다.", e);
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