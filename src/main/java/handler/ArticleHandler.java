package handler;

import domain.Article;
import domain.ArticleDao;
import domain.User;
import webserver.exception.BadRequest;
import webserver.handler.HttpHandler;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;
import webserver.session.HttpSession;

import static webserver.enums.PageMappingPath.INDEX;

public class ArticleHandler implements HttpHandler {
    private static final String TEMPLATE_NAME = "/article/index.html";
    private final ArticleDao articleDao;

    public ArticleHandler(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }


    @Override
    public HttpResponse handleGet(HttpRequest request) {
        return HttpResponse.render(TEMPLATE_NAME);
    }

    @Override
    public HttpResponse handlePost(HttpRequest request) {
        ArticleWriteRequest body = request.getBody(ArticleWriteRequest.class)
                .orElseThrow(() -> new BadRequest("잘못된 요청입니다."));
        User loginUser = (User) request.getSession().get(HttpSession.USER_KEY);

        Article article = Article.create(loginUser, body.content());
        articleDao.insertArticle(article);

        return HttpResponse.redirect(INDEX.path);

    }

    private record ArticleWriteRequest(String content) {
    }
}
