package handler;

import db.ArticleDataManger;
import db.SessionDataManager;
import exception.BaseException;
import exception.SessionErrorCode;
import http.HttpRequestInfo;
import http.HttpResponse;
import http.HttpStatus;
import model.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ArticleCreateHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(ArticleCreateHandler.class);

    private final ArticleDataManger articleDataManger;
    private final SessionDataManager sessionDataManager;

    public ArticleCreateHandler(ArticleDataManger articleDataManger, SessionDataManager sessionDataManager) {
        this.articleDataManger = articleDataManger;
        this.sessionDataManager = sessionDataManager;
    }

    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        logger.info("Article handler called");
        String userId = sessionDataManager.findUserIdBySessionID(request.getSession());

        if (userId == null) {
            logger.error("User id is null");
            throw new BaseException(SessionErrorCode.USER_NOT_FOUND_FOR_SESSION);
        }

        logger.debug("Article Request body : " + request.getBody());
        String content = URLDecoder.decode(request.getBody().substring("article_content=".length()), StandardCharsets.UTF_8);
        Article article = new Article(userId, content, null);
        articleDataManger.addArticle(article);
        logger.debug("Article added to DB");

        HttpResponse response = new HttpResponse();
        response.setStatus(HttpStatus.CREATED);

        return response;
    }

}
