package servlet;

import db.Database;
import model.User;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.HttpStatus;
import webserver.http.servlet.HttpServlet;
import webserver.session.HttpSession;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");
        User user = Database.findUserById(userId);

        if (user != null && user.getPassword().equals(password)) {
            HttpSession session = request.getSession();
            if(session.isNew()) {
                session.setAttribute("user", user);
                response.sendRedirect("/");
            } else {
                response.setStatus(HttpStatus.BAD_REQUEST);
                response.setBody("already logged in");
            }
        } else {
            response.sendRedirect("/login/failed.html");
        }
    }
}
