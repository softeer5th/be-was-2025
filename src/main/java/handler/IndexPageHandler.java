package handler;

import domain.ArticleDao;
import webserver.request.HttpRequest;

/**
 * 최신 게시글을 조회하는 핸들러
 */
public class IndexPageHandler extends ReadArticleHandler {
    /**
     * 생성자
     *
     * @param articleDao 게시글 조회 시 사용하는 ArticleDao 객체
     */
    public IndexPageHandler(ArticleDao articleDao) {
        super(articleDao);
    }

    @Override
    protected Long getArticleId(HttpRequest request) {
        // 최신 게시글 ID를 반환
        return articleDao.findLatestArticleId().orElse(null);
    }
}
