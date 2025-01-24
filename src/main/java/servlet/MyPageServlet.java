package servlet;

import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.servlet.HttpServlet;

public class MyPageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.sendRedirect("/mypage/index.html");
    }
}
