package servlet;

import db.h2.UserStorage;
import model.User;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.HttpStatus;
import webserver.http.servlet.HttpServlet;
import webserver.session.HttpSession;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        java.lang.String userId = request.getParameter("userId");
        java.lang.String password = request.getParameter("password");
        UserStorage userStorage = UserStorage.getInstance();
        User user = userStorage.findUserById(userId);

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
            response.sendRedirect("/login/failed_already_exists.html");
        }
    }
}
