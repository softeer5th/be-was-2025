package api.article;

import api.ApiHandler;
import db.Database;
import global.model.CommonResponse;
import global.model.HttpRequest;
import global.model.LoadResult;
import global.util.JsonUtil;
import model.Article;
import model.Comment;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

public class ArticleWithCommentsPaginationHandler implements ApiHandler {

    private static final Logger logger = LoggerFactory.getLogger(ArticleWithCommentsPaginationHandler.class);

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return "GET".equalsIgnoreCase(httpRequest.method())
                && "/api/articles".equalsIgnoreCase(removeQueryString(httpRequest.path()));
    }

    @Override
    public LoadResult handle(HttpRequest httpRequest) {
        try {
            long totalCount = Database.countArticles();
            if (totalCount == 0) {
                logger.info("등록된 게시글이 하나도 없습니다.");
                return createErrorResponse("ARTICLE-01", "아직 등록된 게시글이 없습니다.");
            }

            int page = parsePageParam(httpRequest.path());
            if (page < 1) page = 1;

            Article article = Database.findArticleByPage(page);
            if (article == null) {
                logger.info("해당 페이지에 게시글이 존재하지 않습니다. page: {}", page);
                return createErrorResponse("ARTICLE-02", "해당 페이지의 게시글이 없습니다.");
            }

            Map<String, Object> data = new ConcurrentHashMap<>();

            User writer = Database.findUserById(article.getUserId());
            String writerName = "(알 수 없음)";
            String writerBase64Image;
            if (writer != null) {
                writerName = writer.getName();
                byte[] writerProfile = writer.getProfileImage();
                if (writerProfile != null && writerProfile.length > 0) {
                    writerBase64Image = Base64.getEncoder().encodeToString(writerProfile);
                    data.put("writerBase64Image", writerBase64Image);
                }
            }

            List<Comment> comments = Database.findCommentsByArticleId(article.getId());
            List<Map<String, Object>> commentDataList = new ArrayList<>();

            for (Comment c : comments) {
                Map<String, Object> cm = new ConcurrentHashMap<>();
                cm.put("commentId", c.getId());
                cm.put("content", c.getContent());

                User commentUser = Database.findUserById(c.getUserId());
                if (commentUser != null) {
                    cm.put("nickname", commentUser.getName());
                    byte[] profileImg = commentUser.getProfileImage();
                    if (profileImg != null && profileImg.length > 0) {
                        String base64Profile = Base64.getEncoder().encodeToString(profileImg);
                        cm.put("base64Profile", base64Profile);
                    }
                } else {
                    cm.put("nickname", c.getUserId());
                }
                commentDataList.add(cm);
            }

            String base64Image ;
            if (article.getImage() != null && article.getImage().length > 0) {
                base64Image = Base64.getEncoder().encodeToString(article.getImage());
                data.put("base64Image", base64Image);
            }

            long totalPages = totalCount;

            data.put("page", page);
            data.put("totalPages", totalPages);
            data.put("articleId", article.getId());
            data.put("writerName", writerName);
            data.put("content", article.getContent());
            data.put("comments", commentDataList);

            logger.info("게시글(ID: {})과 댓글 {}개 정보를 조회했습니다. (page: {})", article.getId(), comments.size(), page);

            CommonResponse response = new CommonResponse(true, null, null, data);
            String json = JsonUtil.toJson(response);

            return new LoadResult(
                    json.getBytes(StandardCharsets.UTF_8),
                    "/api/articles",
                    "application/json",
                    null
            );
        } catch (Exception e) {
            logger.error("게시글 조회 중 오류가 발생했습니다.", e);
            return createErrorResponse("SERVER-ERROR", "게시글 조회 중 오류가 발생했습니다.");
        }
    }

    private String removeQueryString(String path) {
        int idx = path.indexOf('?');
        if (idx != -1) {
            return path.substring(0, idx);
        }
        return path;
    }

    private int parsePageParam(String fullPath) {
        int page = 1;
        int idx = fullPath.indexOf("?page=");
        if (idx != -1) {
            String pageStr = fullPath.substring(idx + 6);
            if (!pageStr.isBlank()) {
                page = Integer.parseInt(pageStr);
            }
        }
        return page;
    }

    private LoadResult createErrorResponse(String code, String message) {
        CommonResponse errorResponse = new CommonResponse(false, code, message, null);
        String json = JsonUtil.toJson(errorResponse);
        return new LoadResult(
                json.getBytes(StandardCharsets.UTF_8),
                "/api/articles",
                "application/json",
                null
        );
    }
}