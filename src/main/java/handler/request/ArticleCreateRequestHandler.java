package handler.request;

import db.ArticleDao;
import db.transaction.Transaction;
import db.transaction.TransactionTemplate;
import http.cookie.Cookie;
import http.enums.HttpStatus;
import http.request.HttpRequest;
import http.request.MultipartPart;
import http.response.HttpResponse;
import http.session.SessionManager;
import model.Article;

import java.nio.charset.StandardCharsets;

public class ArticleCreateRequestHandler implements RequestHandler{
    private final TransactionTemplate transactionTemplate = TransactionTemplate.getInstance();
    private final ArticleDao articleDao = ArticleDao.getInstance();
    private final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return true;
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        Cookie cookie = httpRequest.getCookie("sessionId");
        Long userId = (Long) sessionManager.getSessionAttribute(cookie.getValue(), "userId");

        MultipartPart contentMultipartPart = httpRequest.getMultipartPart("content");
        MultipartPart imageMultipartPart = httpRequest.getMultipartPart("image");

        transactionTemplate.executeWithoutResult(this::createArticle, userId
                ,new String(contentMultipartPart.getBody(), StandardCharsets.UTF_8)
                ,imageMultipartPart.getBody()
        );

        return new HttpResponse.Builder()
                .httpStatus(HttpStatus.SEE_OTHER)
                .location("http://localhost:8080")
                .build();
    }

    private void createArticle(Transaction transaction, Object[] args){
        Long userId = (Long) args[0];
        String content = (String) args[1];
        byte[] imageBytes = (byte[]) args[2];

        articleDao.save(transaction, userId, new Article(content, imageBytes));
    }
}
