package handler;

import db.ArticleStore;
import db.UserStore;
import http.HttpRequest;
import http.HttpResponse;
import http.constant.HttpStatus;
import model.Article;
import model.Session;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.DynamicHtmlEditor;
import util.FileUtils;
import util.MimeType;
import util.SessionUtils;
import util.exception.UserNotFoundException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainHandler implements Handler{
    private static final Logger logger = LoggerFactory.getLogger(MainHandler.class);

    private static final String DEFAULT_PAGE = "0";

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if (!SessionUtils.isLogin(httpRequest)) {
            httpResponse.redirect("/");
            return;
        }
        String path = httpRequest.getPath().toLowerCase();

        File file = FileUtils.findFile(path);
        String content = FileUtils.convertToString(file);
        String extension = FileUtils.getExtension(file);
        MimeType mimeType = MimeType.valueOf(extension.toUpperCase());


        Session session = SessionUtils.findSession(httpRequest);
        User user = UserStore.findUserById(session.userId())
                .orElseThrow(() -> new UserNotFoundException("해당 사용자가 없습니다."));

        String page = httpRequest.getQueries().getOrDefault("page", DEFAULT_PAGE);
        String commentQuery = httpRequest.getQueries().getOrDefault("comment", "hidden");

        List<Article> articles = ArticleStore.findAll();
        int articleNum = articles.size();
        int pageNum = Integer.parseInt(page);

        int prevNum = Math.max(pageNum - 1, 0);
        int nextNum = Math.min(pageNum + 1, articleNum - 1);

        content = DynamicHtmlEditor.edit(content, "username", user.getName());

        Article article = null;

        if (!articles.isEmpty())
            article = articles.get(pageNum);

        String navigationElement = DynamicHtmlEditor.navigationElement(article, prevNum, nextNum, "/main");
        content = DynamicHtmlEditor.edit(content, "navigation", navigationElement);

        String articleElement = DynamicHtmlEditor.articleElement(article);
        content = DynamicHtmlEditor.edit(content, "article", articleElement);

        String commentElement = DynamicHtmlEditor.commentElement(article, getCommentVisible(commentQuery), pageNum, path);
        content = DynamicHtmlEditor.edit(content,"comment", commentElement);

        byte[] body = content.getBytes();
        httpResponse.writeStatusLine(HttpStatus.OK);
        httpResponse.writeBody(body, mimeType.getMimeType());
        httpResponse.send();
    }

    private boolean getCommentVisible(String commentQuery) {
        return commentQuery.equals("all");
    }
}
