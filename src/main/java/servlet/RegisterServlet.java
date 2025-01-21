package servlet;

import db.Database;
import model.User;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.servlet.HttpServlet;

public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String email = request.getParameter("email");

        User user = new User(userId, password, name, email);
        if(Database.findUserById(userId) == null) {
            Database.saveUser(user);
            response.sendRedirect("/");
        } else {
            response.sendRedirect("/registration/failed.html");
        }
    }
}
