package handler.request;

import db.ArticleDao;
import db.transaction.Transaction;
import db.transaction.TransactionTemplate;
import exception.ArticleException;
import exception.ErrorCode;
import http.enums.HttpStatus;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.Article;

public class ArticleRequestHandler implements RequestHandler{
    private final TransactionTemplate transactionTemplate = TransactionTemplate.getInstance();
    private final ArticleDao articleDao = ArticleDao.getInstance();

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return true;
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        String page = httpRequest.getQueryParam("page");
        try {
            transactionTemplate.executeWithoutResult(this::getArticle, Integer.parseInt(page));
        }catch(ArticleException e){
            return new HttpResponse.Builder()
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }

        return new HttpResponse.Builder()
                .httpStatus(HttpStatus.SEE_OTHER)
                .location(String.format("http://localhost:8080?page=%s", page))
                .build();
    }

    private void getArticle(Transaction transaction, Object[] args){
        Integer page = (Integer) args[0];
        articleDao.findArticlesWithPagination(transaction, page, 1).orElseThrow(() -> new ArticleException(ErrorCode.NOT_EXIST_ARTICLE));
    }
}
