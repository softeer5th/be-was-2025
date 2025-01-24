package servlet;

import db.h2.ArticleStorage;
import model.Article;
import model.User;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.HttpStatus;
import webserver.http.servlet.HttpServlet;
import webserver.session.HttpSession;

public class CreateArticleServlet extends HttpServlet {
    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendError(HttpStatus.UNAUTHORIZED, "You need to login first");
            return;
        }
        User user = (User) session.getAttribute("user");
        java.lang.String content = request.getParameter("content");

        ArticleStorage articleStorage = ArticleStorage.getInstance();
        Article article = new Article(user, content);
        articleStorage.insert(article);

        response.sendRedirect("/");
    }
}
