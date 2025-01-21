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
        User user = Database.findUserById(session.userId())
                .orElseThrow(() -> new UserNotFoundException("해당 사용자가 없습니다."));

        String page = httpRequest.getQueries().getOrDefault("page", DEFAULT_PAGE);

        List<Article> articles = ArticleStore.findAll();
        int articleNum = articles.size();
        int pageNum = Integer.parseInt(page);

        Article article = articles.get(pageNum);


        int prevNum = Math.max(pageNum - 1, 0);
        int nextNum = Math.min(pageNum + 1, articleNum - 1);

        content = DynamicHtmlEditor.edit(content, "username", user.getName());
        content = DynamicHtmlEditor.edit(content, "author", article.getUser().getName());
        content = DynamicHtmlEditor.edit(content, "content", article.getContent());
        content = DynamicHtmlEditor.edit(content, "prevPage", String.valueOf(prevNum));
        content = DynamicHtmlEditor.edit(content, "nextPage", String.valueOf(nextNum));

        byte[] body = content.getBytes();
        httpResponse.writeStatusLine(HttpStatus.OK);
        httpResponse.writeBody(body, mimeType.getMimeType());
        httpResponse.send();
    }
}
