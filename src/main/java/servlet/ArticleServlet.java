package servlet;

import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.HttpStatus;
import webserver.http.servlet.HttpServlet;
import webserver.session.HttpSession;

public class ArticleServlet extends HttpServlet {
    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        HttpSession session = request.getSession(false);
        response.setStatus(HttpStatus.FOUND);
        if (session == null) {
            response.setHeader("Location", "/login/index.html");
        } else {
            response.setHeader("Location", "/article/index.html");
        }
    }
}
