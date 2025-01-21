package handler;

import db.ArticleStore;
import http.HttpRequest;
import http.HttpResponse;
import http.constant.HttpStatus;
import model.Article;
import util.DynamicHtmlEditor;
import util.FileUtils;
import util.MimeType;
import util.SessionUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class HomeHandler implements Handler {
    private static final String DEFAULT_PAGE = "0";

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if (SessionUtils.isLogin(httpRequest)) {
            httpResponse.redirect("/main");
            return;
        }
        String path = httpRequest.getPath().toLowerCase();

        File file = FileUtils.findFile(path);
        String content = FileUtils.convertToString(file);
        String extension = FileUtils.getExtension(file);
        MimeType mimeType = MimeType.valueOf(extension.toUpperCase());

        String page = httpRequest.getQueries().getOrDefault("page", DEFAULT_PAGE);

        List<Article> articles = ArticleStore.findAll();
        int articleNum = articles.size();
        int pageNum = Integer.parseInt(page);

        Article article = articles.get(pageNum);

        int prevNum = Math.max(pageNum - 1, 0);
        int nextNum = Math.min(pageNum + 1, articleNum - 1);

        content = DynamicHtmlEditor.edit(content, "author", article.getUser().getName());
        content = DynamicHtmlEditor.edit(content, "content", article.getContent());
        content = DynamicHtmlEditor.edit(content, "prevPage", String.valueOf(prevNum));
        content = DynamicHtmlEditor.edit(content, "nextPage", String.valueOf(nextNum));

        String commentElement = DynamicHtmlEditor.commentElement(article.getComments());

        content = DynamicHtmlEditor.edit(content,"comment", commentElement);


        byte[] body = content.getBytes();
        httpResponse.writeStatusLine(HttpStatus.OK);
        httpResponse.writeBody(body, mimeType.getMimeType());
        httpResponse.send();
    }
}
