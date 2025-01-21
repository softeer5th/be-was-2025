package handler;

import db.ArticleStore;
import db.CommentStore;
import db.Database;
import http.HttpRequest;
import http.HttpResponse;
import http.constant.HttpStatus;
import model.Article;
import model.Comment;
import model.Session;
import model.User;
import util.DynamicHtmlEditor;
import util.FileUtils;
import util.MimeType;
import util.SessionUtils;
import util.exception.ArticleNotFoundException;
import util.exception.UserNotFoundException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class CommentHandler implements Handler {
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

        String articleId = httpRequest.getQueries().get("article");

        content = DynamicHtmlEditor.edit(content, "articleId", articleId);

        byte[] body = content.getBytes();
        httpResponse.writeStatusLine(HttpStatus.OK);
        httpResponse.writeBody(body, mimeType.getMimeType());
        httpResponse.send();
    }

    public void postComment(HttpRequest httpRequest, HttpResponse httpResponse) throws UnsupportedEncodingException {
        if (!SessionUtils.isLogin(httpRequest)) {
            httpResponse.redirect("/");
            return;
        }
        Session session = SessionUtils.findSession(httpRequest);
        User user = Database.findUserById(session.userId())
                .orElseThrow(() -> new UserNotFoundException("해당 사용자가 없습니다."));
        Map<String, String> data = parseBody(httpRequest);

        String commentContent = data.get("content");
        String articleId = data.get("article");

        Article article = ArticleStore.findArticleById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException("해당 게시글이 없습니다."));

        Comment comment = new Comment(commentContent, user, article);
        CommentStore.addComment(comment);

        article.getComments().add(comment);
        ArticleStore.addArticle(article);
        httpResponse.redirect("/main");
    }

    private Map<String, String> parseBody(HttpRequest request) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        String body = new String(request.getBody());
        body = URLDecoder.decode(body, "utf-8");
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
