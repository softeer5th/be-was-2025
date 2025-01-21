package handler;


import domain.Article;
import domain.ArticleDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.enums.HttpStatusCode;
import webserver.exception.NotFound;
import webserver.handler.HttpHandler;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;
import webserver.view.ModelAndTemplate;

import static webserver.enums.PageMappingPath.INDEX;

/**
 * / 경로 요청을 처리하는 핸들러
 */
public class ReadArticleHandler implements HttpHandler {
    private static final String TEMPLATE_NAME = "/index.html";
    private static final Logger log = LoggerFactory.getLogger(ReadArticleHandler.class);
    private final ArticleDao articleDao;

    /**
     * 생성자
     *
     * @param articleDao 게시글 조회 시 사용하는 ArticleDao 객체
     */
    public ReadArticleHandler(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }


    /**
     * 최신 게시글을 조회하여 index.html 템플릿에 렌더링하여 반환한다.
     *
     * @param request
     * @return
     */
    @Override
    public HttpResponse handleGet(HttpRequest request) {
        Long articleId = null;
        if (isIndexPage(request)) {
            articleId = articleDao.findLatestArticleId().orElse(null);
        } else {
            articleId = request.getPathVariable("articleId")
                    .map(Long::parseLong)
                    .orElseThrow(() -> new NotFound("게시글을 찾을 수 없습니다."));

        }
        return createArticleResponse(articleId);
    }

    private HttpResponse createArticleResponse(Long articleId) {
        Article article = articleDao.findArticleById(articleId).orElse(null);
        Long nextArticleId = articleDao.findNextArticleId(articleId).orElse(null);
        Long previousArticleId = articleDao.findPreviousArticleId(articleId).orElse(null);
        ModelAndTemplate template = new ModelAndTemplate(TEMPLATE_NAME);
        template.addAttribute("article", article);
        template.addAttribute("nextArticleId", nextArticleId);
        template.addAttribute("previousArticleId", previousArticleId);
        HttpResponse response = new HttpResponse(HttpStatusCode.OK);
        response.renderTemplate(template);
        return response;
    }

    private boolean isIndexPage(HttpRequest request) {
        return INDEX.path.equals(request.getRequestTarget().getPath());
    }
}
