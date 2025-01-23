package handler;

import domain.Article;
import domain.ArticleDao;
import domain.User;
import webserver.exception.BadRequest;
import webserver.handler.HttpHandler;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;
import webserver.session.HttpSession;

import static enums.PageMappingPath.readArticlePath;

/**
 * 게시글 작성을 처리하는 핸들러
 */
public class WriteArticleHandler implements HttpHandler {
    private static final String TEMPLATE_NAME = "/article/index.html";
    private final ArticleDao articleDao;

    public WriteArticleHandler(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }

    /**
     * 게시글 작성 페이지를 보여준다
     */
    @Override
    public HttpResponse handleGet(HttpRequest request) {
        return HttpResponse.render(TEMPLATE_NAME);
    }

    /**
     * 게시글 작성 요청을 처리한다.
     */
    @Override
    public HttpResponse handlePost(HttpRequest request) {
        ArticleWriteRequest body = request.getBody(ArticleWriteRequest.class)
                .orElseThrow(() -> new BadRequest("잘못된 요청입니다."));
        User loginUser = (User) request.getSession().get(HttpSession.USER_KEY);

        Article article = Article.create(loginUser, body.content());
        articleDao.insertArticle(article);

        return HttpResponse.redirect(readArticlePath(article.getArticleId()));

    }

    private record ArticleWriteRequest(String content) {
    }
}
