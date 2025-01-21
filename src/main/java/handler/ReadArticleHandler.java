package handler;


import domain.Article;
import domain.ArticleDao;
import domain.Comment;
import domain.CommentDao;
import webserver.enums.HttpStatusCode;
import webserver.exception.NotFound;
import webserver.handler.HttpHandler;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;
import webserver.view.ModelAndTemplate;

import java.util.List;

/**
 * 게시글 읽기 요청을 처리하는 핸들러
 */
public class ReadArticleHandler implements HttpHandler {
    private static final String TEMPLATE_NAME = "/index.html";
    protected final ArticleDao articleDao;
    private final CommentDao commentDao;

    /**
     * 생성자
     *
     * @param articleDao 게시글 조회 시 사용하는 ArticleDao 객체
     */
    public ReadArticleHandler(ArticleDao articleDao, CommentDao commentDao) {
        this.articleDao = articleDao;
        this.commentDao = commentDao;
    }


    /**
     * 게시글 페이지를 보여준다
     */
    @Override
    public HttpResponse handleGet(HttpRequest request) {
        Long articleId = getArticleId(request);
        return createArticleResponse(articleId);
    }


    /**
     * 게시글 ID를 반환한다
     */
    protected Long getArticleId(HttpRequest request) {
        return request.getPathVariable("articleId")
                .map(Long::parseLong)
                .orElseThrow(() -> new NotFound("게시글을 찾을 수 없습니다."));
    }

    private HttpResponse createArticleResponse(Long articleId) {
        Article article = articleDao.findArticleById(articleId).orElse(null);
        Long nextArticleId = articleDao.findNextArticleId(articleId).orElse(null);
        Long previousArticleId = articleDao.findPreviousArticleId(articleId).orElse(null);

        List<Comment> comments = commentDao.findAllByArticle(article);

        ModelAndTemplate template = new ModelAndTemplate(TEMPLATE_NAME);
        template.addAttribute("article", article);
        template.addAttribute("nextArticleId", nextArticleId);
        template.addAttribute("previousArticleId", previousArticleId);
        template.addAttribute("comments", comments);
        HttpResponse response = new HttpResponse(HttpStatusCode.OK);
        response.renderTemplate(template);
        return response;
    }

}
