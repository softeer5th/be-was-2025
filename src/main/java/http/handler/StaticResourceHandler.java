package http.handler;

import com.sun.jdi.request.InvalidRequestStateException;
import db.Database;
import db.SessionDB;
import http.enums.ErrorMessage;
import http.enums.HttpResponseStatus;
import http.request.HttpRequest;
import http.request.TargetInfo;
import http.response.DynamicHtmlBuilder;
import http.response.HttpResponse;
import model.Article;
import model.Comment;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileUtil;
import util.HttpRequestUtil;
import util.JwtUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticResourceHandler implements Handler {
    private final String staticResourcePath;
    private static final String INDEX_HTML = "/index.html";
    private static final String LOGIN_PAGE_PATH = "/login";
    private static final Map<String, String> needLoginPage = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(StaticResourceHandler.class);

    private static StaticResourceHandler instance;

    private StaticResourceHandler(String staticResourcePath) {
        this.staticResourcePath = staticResourcePath;
    }

    public static StaticResourceHandler getInstance(String staticResourcePath) {
        if (instance == null) {
            synchronized (StaticResourceHandler.class) {
                if (instance == null) {
                    instance = new StaticResourceHandler(staticResourcePath);
                }
            }
        }
        return instance;
    }

    static {
        needLoginPage.put("/article", LOGIN_PAGE_PATH);
        needLoginPage.put("/mypage", LOGIN_PAGE_PATH);
        needLoginPage.put("/comment", LOGIN_PAGE_PATH);
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        TargetInfo target = request.getTarget();
        String path = staticResourcePath + target.getPath();
        HttpResponse response;
        HttpResponse.Builder builder = new HttpResponse.Builder();

        path = HttpRequestUtil.buildPath(path);
        String type = HttpRequestUtil.getType(path); // 파일 유형 별로 Content-Type 할당

        try {
            for (Map.Entry<String, String> entry : needLoginPage.entrySet()) {
                if (path.equals(staticResourcePath + entry.getKey() + INDEX_HTML)) {
                    String sid = HttpRequestUtil.getCookieValueByKey(request, "sid");
                    String userId = JwtUtil.getIdFromToken(sid);
                    User user = SessionDB.getUser(sid);
                    Database.findUserById(userId);
                    if (!userId.equals(user.getUserId())) throw new InvalidRequestStateException("로그인 되지 않은 사용자입니다.");
                }
            }

            byte[] body = FileUtil.fileToByteArray(path);
            if (body != null) {
                if (path.endsWith(".html")) {
                    String pageId;
                    Article article;
                    Map<String, String> params = request.getTarget().getParams();
                    if ((pageId = params.get("pageId")) != null) {
                        article = Database.getArticleByPageId(Integer.parseInt(pageId));
                        if (article == null) article = Database.getLatestArticle();
                    } else if ((pageId = params.get("prev")) != null) {
                        article = Database.getPreviousArticle(Integer.parseInt(pageId));
                        if (article == null) article = Database.getEarliestArticle();
                    } else if ((pageId = params.get("next")) != null) {
                        article = Database.getNextArticle(Integer.parseInt(pageId));
                        if (article == null) article = Database.getLatestArticle();
                    } else {
                        article = Database.getLatestArticle();
                    }
                    if (article != null) {
                        List<Comment> comments = Database.getComments(article.getId());
                        DynamicHtmlBuilder htmlBuilder = new DynamicHtmlBuilder(new String(body), request, Map.of(
                                "username", Database.findUserById(article.getUserId()).getName(),
                                "articlephoto", article.getPhoto(),
                                "article", article.getContent(),
                                "articleId", Integer.toString(article.getId()),
                                "comments", comments
                        ));
                        body = htmlBuilder.build().getBytes();
                    }
                }

                response = builder
                        .successResponse(HttpResponseStatus.OK, type, body)
                        .build();
            } else {
                response = builder
                        .errorResponse(HttpResponseStatus.NOT_FOUND, ErrorMessage.NOT_FOUND_PATH_AND_FILE)
                        .build();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response = builder
                    .redirectResponse(HttpResponseStatus.FOUND, LOGIN_PAGE_PATH)
                    .build();
        }
        return response;
    }
}
