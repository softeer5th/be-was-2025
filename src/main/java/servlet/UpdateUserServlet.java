package servlet;

import db.Database;
import model.User;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.HttpStatus;
import webserver.http.servlet.HttpServlet;
import webserver.session.HttpSession;

public class UpdateUserServlet extends HttpServlet {
    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        HttpSession session = request.getSession(false);

        if(session == null) {
            response.setStatus(HttpStatus.FOUND);
            response.setHeader("location", "/auth/failed_session_expired.html");
            return;
        }

        User user = (User) session.getAttribute("user");

        if(user == null) {
            response.setStatus(HttpStatus.FOUND);
            response.setHeader("location", "/auth/failed_session_expired.html");
            return;
        }

        String newName = request.getParameter("newName");
        String newPassword = request.getParameter("newPassword");
        String newPasswordConfirm = request.getParameter("newPasswordConfirm");

        if(newName == null) {
            response.setStatus(HttpStatus.FOUND);
            response.setHeader("location", "/mypage/failed_empty_field.html");
            return;
        }

        if(newPassword != null && !newPassword.equals(newPasswordConfirm)) {
            response.setStatus(HttpStatus.FOUND);
            response.setHeader("location", "/mypage/failed_incorrect_password.html");
            return;
        }

        user.changeName(newName);
        user.changePassword(newPassword);

        Database.saveUser(user);

        response.setStatus(HttpStatus.FOUND);
        if(newPassword != null) {
            response.setHeader("location", "/mypage/success.html");
        } else {
            response.setHeader("location", "/");
        }

        session.invalidate();
    }
}
