package controller;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wasframework.Mapping;
import wasframework.PathVariable;
import webserver.httpserver.HttpMethod;
import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;
import webserver.httpserver.StatusCode;

import java.io.File;
import java.io.IOException;

import static db.Database.addUser;
import static utils.FileUtils.getFile;

public class SignUpController {

    private static final Logger log = LoggerFactory.getLogger(SignUpController.class);

    @Mapping(path = "/registration", method = HttpMethod.GET)
    public void registerPage(HttpRequest request, HttpResponse response) throws IOException {
        response.setStatusCode(StatusCode.OK);
        response.setHeader("Content-Type", "text/html");

        File file = new File("src/main/resources/static/registration/index.html");
        byte[] readFile = getFile(file);
        response.setBody(readFile);
    }

    @Mapping(path = "/create", method = HttpMethod.GET)
    public void createUser(HttpRequest request, HttpResponse response) throws IOException {
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

    @Mapping(path = "/success", method = HttpMethod.GET)
    public void signUpSuccess(HttpRequest request, HttpResponse response) throws IOException {
        response.setStatusCode(StatusCode.OK);
        response.setHeader("Content-Type", "text/html");

        File file = new File("src/main/resources/static/registration/success.html");
        byte[] readFile = getFile(file);
        response.setBody(readFile);
    }

    @Mapping(path = "/test/{id}", method = HttpMethod.GET)
    public void signUpTest(HttpRequest request, HttpResponse response, @PathVariable("id") int id) {
        response.setStatusCode(StatusCode.OK);
        response.setHeader("Content-Type", "text/html");
        String body = "<h1>id value is " + id + ". </h1>";
        response.setBody(body.getBytes());
    }
}
