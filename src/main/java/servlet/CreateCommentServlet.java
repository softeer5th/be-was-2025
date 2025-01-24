package servlet;

import db.h2.ArticleStorage;
import db.h2.CommentStorage;
import model.Article;
import model.Comment;
import model.User;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.HttpStatus;
import webserver.http.servlet.HttpServlet;
import webserver.session.HttpSession;

public class CreateCommentServlet extends HttpServlet {
    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        HttpSession httpSession = request.getSession(false);

        if(httpSession == null || httpSession.getAttribute("user") == null) {
            response.sendError(HttpStatus.UNAUTHORIZED, "you are not logged in");
            return;
        }

        User user = (User) httpSession.getAttribute("user");
        java.lang.String content = request.getParameter("content");
        long articleId = Long.parseLong(request.getParameter("articleId"));
        java.lang.String index = request.getParameter("index");

        ArticleStorage articleStorage = ArticleStorage.getInstance();
        Article article = articleStorage.findArticleById(articleId);
        if(article == null) {
            response.sendError(HttpStatus.BAD_REQUEST, "You’re trying to comment on the wrong article.");
        }

        Comment comment = new Comment(user, article, content);

        CommentStorage commentStorage = CommentStorage.getInstance();
        commentStorage.insert(comment);

        response.sendRedirect("/?index=" + index);
    }
}
