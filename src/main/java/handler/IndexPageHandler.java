package handler;

import domain.ArticleDao;
import domain.CommentDao;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

import java.util.Optional;

/**
 * 최신 게시글을 조회하는 핸들러
 */
public class IndexPageHandler extends ReadArticleHandler {
    private static final String EMPTY_ARTICLE_TEMPLATE_NAME = "/noArticle.html";

    /**
     * 생성자
     *
     * @param articleDao 게시글 조회 시 사용하는 ArticleDao 객체
     */
    public IndexPageHandler(ArticleDao articleDao, CommentDao commentDao) {
        super(articleDao, commentDao);
    }

    @Override
    public HttpResponse handleGet(HttpRequest request) {
        Optional<Long> latestArticleId = articleDao.findLatestArticleId();
        if (latestArticleId.isEmpty()) {
            return HttpResponse.render(EMPTY_ARTICLE_TEMPLATE_NAME);
        }
        return createArticleResponse(latestArticleId.get());
    }
}
