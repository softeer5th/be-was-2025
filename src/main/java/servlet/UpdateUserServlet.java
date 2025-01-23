package servlet;

import db.h2.UserStorage;
import model.User;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
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

        if(newPassword != null && !newPassword.isBlank() && !newPassword.equals(newPasswordConfirm)) {
            response.sendRedirect("/mypage/failed_incorrect_password.html");
            return;
        }

        if(newPassword != null && !newPassword.isBlank() && !User.validatePassword(newPassword)) {
            response.sendRedirect("/mypage/failed_invalid_password.html");
            return;
        }

        user.changeName(newName);
        user.changePassword(newPassword);

        UserStorage userStorage = UserStorage.getInstance();
        userStorage.update(user);

        if(newPassword != null && !newPassword.isBlank()) {
            response.sendRedirect("/mypage/success.html");
            session.invalidate();
        } else {
            response.sendRedirect("/");
        }
    }
}
