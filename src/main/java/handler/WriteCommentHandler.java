package handler;

import db.TransactionFactory;
import domain.*;
import webserver.enums.HttpStatusCode;
import webserver.exception.BadRequest;
import webserver.exception.NotFound;
import webserver.handler.HttpHandler;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;
import webserver.session.HttpSession;
import webserver.view.ModelAndTemplate;

import static enums.PageMappingPath.readArticlePath;
import static util.CommonUtil.hasLength;
import static util.CommonUtil.isBlank;
import static util.ExceptionUtil.ignoreException;

/**
 * 댓글 작성을 처리하는 핸들러
 */
public class WriteCommentHandler implements HttpHandler {
    private static final String TEMPLATE_NAME = "/comment/index.html";

    private final TransactionFactory factory;
    private final ArticleDao articleDao;
    private final CommentDao commentDao;

    public WriteCommentHandler(TransactionFactory factory, ArticleDao articleDao, CommentDao commentDao) {
        this.factory = factory;
        this.articleDao = articleDao;
        this.commentDao = commentDao;
    }

    /**
     * 댓글 작성 페이지를 보여준다
     */
    @Override
    public HttpResponse handleGet(HttpRequest request) {
        Long articleId = request.getPathVariable("articleId")
                .flatMap(id -> ignoreException(() -> Long.parseLong(id)))
                .orElseThrow(() -> new BadRequest("잘못된 요청입니다."));
        Article article = articleDao.findArticleById(articleId)
                .orElseThrow(() -> new NotFound("게시글을 찾을 수 없습니다."));
        ModelAndTemplate template = new ModelAndTemplate(TEMPLATE_NAME);
        template.addAttribute("article", article);
        HttpResponse response = new HttpResponse(HttpStatusCode.OK);
        response.renderTemplate(template);
        return response;
    }

    /**
     * <pre>
     * 댓글 작성 요청을 처리한다.
     * body 형식
     * String content: 댓글 내용
     * </pre>
     */
    @Override
    public HttpResponse handlePost(HttpRequest request) {
        return factory.runInTransaction(articleDao, commentDao, (articleDao, commentDao) -> {
            Long articleId = request.getPathVariable("articleId").map(Long::parseLong)
                    .orElseThrow(() -> new BadRequest("잘못된 요청입니다."));

            CommentWriteRequest body = request.getBody(CommentWriteRequest.class)
                    .orElseThrow(() -> new BadRequest("잘못된 요청입니다."));
            body.validate();

            Article article = articleDao.findArticleById(articleId)
                    .orElseThrow(() -> new BadRequest("잘못된 요청입니다."));

            User user = (User) request.getSession().get(HttpSession.USER_KEY);

            Comment comment = Comment.create(user, body.content(), article);
            commentDao.insertComment(comment);

            return HttpResponse.redirect(readArticlePath(articleId));
        });
    }


    private record CommentWriteRequest(String content) {
        void validate() {
            if (isBlank(content) || !hasLength(content, 1, 300)) {
                throw new BadRequest("댓글은 1자 이상 300자 이하여야 합니다.");
            }
        }
    }
}
