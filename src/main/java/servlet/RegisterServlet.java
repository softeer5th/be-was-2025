package servlet;

import db.Database;
import model.User;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.HttpStatus;
import webserver.http.servlet.HttpServlet;

public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String email = request.getParameter("email");

        User user = new User(userId, password, name, email);
        if(Database.findUserById(userId) == null) {
            Database.addUser(user);
            response.setStatus(HttpStatus.CREATED);
        } else {
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setBody("이미 존재하는 아이디 입니다.");
        }
        response.send();
    }
}
