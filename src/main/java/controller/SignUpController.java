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
import java.nio.charset.StandardCharsets;

import static db.Database.addUser;
import static utils.FileUtils.getFile;

public class SignUpController {

    private static final Logger log = LoggerFactory.getLogger(SignUpController.class);

    @Mapping(path = "/registration", method = HttpMethod.GET)
    public void registerPage(HttpRequest request, HttpResponse response) throws IOException {
        response.setStatusCode(StatusCode.OK);
        response.setHeader("Content-Type", "text/html; charset=utf-8");

        File file = new File("src/main/resources/static/registration/index.html");
        byte[] readFile = getFile(file);
        response.setBody(readFile);
    }

    @Mapping(path = "/user/create", method = HttpMethod.POST)
    public void createUser(HttpRequest request, HttpResponse response) throws IOException {
        String userId = request.getParameter(User.USER_ID);
        String password = request.getParameter(User.PASSWORD);
        String name = request.getParameter(User.USERNAME);
        String email = null;
        User user = new User(userId, password, name, email);
        addUser(user);

        log.info("User created: " + user);
        response.setLocation("/");
    }

    @Mapping(path = "/success", method = HttpMethod.GET)
    public void signUpSuccess(HttpRequest request, HttpResponse response) throws IOException {
        response.setStatusCode(StatusCode.OK);
        response.setHeader("Content-Type", "text/html;charset=utf-8");

        File file = new File("src/main/resources/static/registration/success.html");
        byte[] readFile = getFile(file);
        response.setBody(readFile);
    }

    @Mapping(path = "/test/{id}", method = HttpMethod.GET)
    public void signUpTest(HttpRequest request, HttpResponse response, @PathVariable("id") int id) {
        response.setStatusCode(StatusCode.OK);
        response.setHeader("Content-Type", "text/html; charset=utf-8");
        String body = "<h1>id 값은 " + id + "입니다. </h1>";
        response.setBody(body.getBytes(StandardCharsets.UTF_8));
    }
}
