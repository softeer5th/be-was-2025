package handler;

import db.ArticleStore;
import db.Database;
import http.HttpRequest;
import http.HttpResponse;
import http.constant.HttpStatus;
import model.Article;
import model.Session;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileUtils;
import util.SessionUtils;
import util.exception.UserNotFoundException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ArticleHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(ArticleHandler.class);

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if (!SessionUtils.isLogin(httpRequest)) {
            httpResponse.redirect("/login");
            return;
        }

        String path = httpRequest.getPath().toLowerCase();

        File file = FileUtils.findFile(path);

        httpResponse.writeStatusLine(HttpStatus.OK);
        httpResponse.writeBody(file);
        httpResponse.send();
    }

    public void postArticle(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (!SessionUtils.isLogin(httpRequest)) {
            httpResponse.redirect("/login");
            return;
        }
        Session session = SessionUtils.findSession(httpRequest);
        User user = Database.findUserById(session.userId())
                .orElseThrow(() -> new UserNotFoundException("해당 사용자가 없습니다."));
        Map<String, String> data = parseBody(httpRequest);

        String content = data.get("content");

        Article article = new Article(content, user);
        logger.debug("Created Article= id:{}, content:{}, username:{}", article.getArticleId(), article.getContent(),
                article.getUser().getName());
        ArticleStore.addArticle(article);

        httpResponse.redirect("/main");
    }

    private Map<String, String> parseBody(HttpRequest request) {
        Map<String, String> map = new HashMap<>();
        String body = request.getBody();
        String[] tokens = body.split("&");
        for(String token: tokens) {
            String[] items = token.split("=");
            String key = items[0].trim();
            String value = items.length > 1 ? items[1].trim() : null;
            map.put(key, value);
        }
        return map;
    }
}
