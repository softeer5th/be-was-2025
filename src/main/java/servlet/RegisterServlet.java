package servlet;

import db.h2.UserStorage;
import model.User;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.servlet.HttpServlet;
import webserver.session.HttpSession;

public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        java.lang.String userId = request.getParameter("userId");
        java.lang.String password = request.getParameter("password");
        java.lang.String name = request.getParameter("name");
        java.lang.String email = request.getParameter("email");

        User user = new User(userId, password, name, email);

        if(!User.validateUserId(user.getUserId())) {
            response.sendRedirect("/registration/failed_invalid_id.html");
            return;
        }

        if(!User.validateEmail(user.getEmail())) {
            response.sendRedirect("/registration/failed_invalid_email.html");
            return;
        }

        if(!User.validatePassword(user.getPassword())) {
            response.sendRedirect("/registration/failed_invalid_password.html");
            return;
        }

        UserStorage userStorage = UserStorage.getInstance();

        if(userStorage.findUserById(userId) == null) {
            HttpSession session = request.getSession();
            user = userStorage.insert(user);
            session.setAttribute("user", user);
            response.sendRedirect("/");
        } else {
            response.sendRedirect("/registration/failed_already_exists.html");
        }
    }
}
