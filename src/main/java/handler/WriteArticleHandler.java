package handler;

import domain.Article;
import domain.ArticleDao;
import domain.User;
import webserver.exception.BadRequest;
import webserver.handler.HttpHandler;
import webserver.request.FileUploader;
import webserver.request.HttpRequest;
import webserver.request.Multipart;
import webserver.response.HttpResponse;
import webserver.session.HttpSession;

import static enums.PageMappingPath.readArticlePath;
import static util.CommonUtil.hasLength;
import static util.CommonUtil.isBlank;

/**
 * 게시글 작성을 처리하는 핸들러
 */
public class WriteArticleHandler implements HttpHandler {
    private static final String TEMPLATE_NAME = "/article/index.html";
    private final ArticleDao articleDao;
    private final FileUploader fileUploader;

    public WriteArticleHandler(ArticleDao articleDao, FileUploader fileUploader) {
        this.articleDao = articleDao;
        this.fileUploader = fileUploader;
    }

    /**
     * 게시글 작성 페이지를 보여준다
     */
    @Override
    public HttpResponse handleGet(HttpRequest request) {
        return HttpResponse.render(TEMPLATE_NAME);
    }

    /**
     * <pre>
     * 게시글 작성 요청을 처리한다.
     * body 형식
     * String content: 게시글 내용
     * File articleImage: 게시글 이미지
     * </pre>
     */
    @Override
    public HttpResponse handlePost(HttpRequest request) {
        ArticleWriteRequest body = parseRequest(request);
        body.validate();
        User loginUser = (User) request.getSession().get(HttpSession.USER_KEY);

        Article article = Article.create(loginUser, body.content(), body.articleImagePath());
        articleDao.insertArticle(article);

        return HttpResponse.redirect(readArticlePath(article.getArticleId()));

    }

    private ArticleWriteRequest parseRequest(HttpRequest request) {
        Multipart multipart = request.getMultipart();
        String content = multipart.getString("content");
        String articleImagePath = multipart.saveFile("articleImage", fileUploader);
        return new ArticleWriteRequest(content, articleImagePath);
    }

    private record ArticleWriteRequest(String content, String articleImagePath) {
        void validate() {
            if (isBlank(content) || !hasLength(content, 1, 1000)) {
                throw new BadRequest("게시글 내용은 1자 이상 1000자 이하로 입력해주세요.");
            }
        }
    }
}
