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
        String commentQuery = httpRequest.getQueries().getOrDefault("comment", "hidden");

        boolean showAll = getCommentVisible(commentQuery);

        List<Article> articles = ArticleStore.findAll();
        int articleNum = articles.size();
        int pageNum = Integer.parseInt(page);

        int prevNum = Math.max(pageNum - 1, 0);
        int nextNum = Math.min(pageNum + 1, articleNum - 1);

        Article article = null;
        if (!articles.isEmpty())
            article = articles.get(pageNum);

        String navigationElement = DynamicHtmlEditor.navigationElement(article, prevNum, nextNum, "/");
        content = DynamicHtmlEditor.edit(content, "navigation", navigationElement);

        String articleElement = DynamicHtmlEditor.articleElement(article);
        content = DynamicHtmlEditor.edit(content, "article", articleElement);

        String commentElement = DynamicHtmlEditor.commentElement(article, showAll, pageNum, path);
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
