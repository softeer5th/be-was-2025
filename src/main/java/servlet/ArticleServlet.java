package servlet;

import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.servlet.HttpServlet;
import webserver.session.HttpSession;

public class ArticleServlet extends HttpServlet {
    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("/login/index.html");
        } else {
            response.sendRedirect("/article/index.html");
        }
    }
}
