package servlet;

import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;
import webserver.httpserver.StatusCode;

import java.io.File;
import java.io.IOException;

import static db.Database.addUser;
import static utils.FileUtils.getFile;

public class CreateServlet implements Servlet {

    private static final Logger log = LoggerFactory.getLogger(CreateServlet.class);

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String email = null;
        User user = new User(userId, password, name, email);
        addUser(user);

        log.info("User created: " + user);
        response.setStatusCode(StatusCode.SEE_OTHER);
        response.setHeader("Location", "/success");
    }
}
