package handler;

import db.ArticleDataManger;
import db.SessionDataManager;
import http.HttpRequestInfo;
import http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArticleHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(ArticleHandler.class);

    private final ArticleDataManger articleDataManger;
    private final SessionDataManager sessionDataManager;

    public ArticleHandler(ArticleDataManger articleDataManger, SessionDataManager sessionDataManager) {
        this.articleDataManger = articleDataManger;
        this.sessionDataManager = sessionDataManager;
    }

    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        logger.info("Article handler called");
        return null;
    }
}
