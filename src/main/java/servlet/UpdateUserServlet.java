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
            response.sendRedirect("/auth/failed_session_expired.html");
            return;
        }

        User user = (User) session.getAttribute("user");

        if(user == null) {
            response.sendRedirect("/auth/failed_session_expired.html");
            return;
        }

        String newName = request.getParameter("newName");
        String newPassword = request.getParameter("newPassword");
        String newPasswordConfirm = request.getParameter("newPasswordConfirm");

        if(newName == null) {
            response.sendRedirect("/mypage/failed_empty_field.html");
            return;
        }

        if(newPassword != null && !newPassword.equals(newPasswordConfirm)) {
            response.sendRedirect("/mypage/failed_incorrect_password.html");
            return;
        }

        user.changeName(newName);
        user.changePassword(newPassword);

        Database.saveUser(user);

        if(newPassword != null) {
            response.sendRedirect("/mypage/success.html");
            session.invalidate();
        } else {
            response.sendRedirect("/");
        }
    }
}
