package servlet;

import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.HttpStatus;
import webserver.http.servlet.HttpServlet;
import webserver.session.HttpSession;

public class LogoutServlet extends HttpServlet {
    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        HttpSession session = request.getSession(false);

        if(session == null) {
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setBody("logout failed. you are not logged in.");
        } else {
            session.invalidate();
            response.setStatus(HttpStatus.FOUND);
            response.setHeader("Location", "/");
            response.setBody("logout successful");
        }
    }
}
