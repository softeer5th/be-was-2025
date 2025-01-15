package controller;

import db.Database;
import model.User;
import wasframework.HttpSession;
import wasframework.Mapping;
import webserver.httpserver.HttpMethod;
import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;
import webserver.httpserver.StatusCode;
import webserver.httpserver.header.Cookie;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static utils.FileUtils.getFile;

public class UserController {


    @Mapping(path = "/login", method = HttpMethod.GET)
    public void loginPage(HttpRequest request, HttpResponse response) throws IOException {
        response.setStatusCode(StatusCode.OK);
        response.setHeader("Content-Type", "text/html; charset=utf-8");

        File file = new File("src/main/resources/static/login/index.html");
        byte[] readFile = getFile(file);
        response.setBody(readFile);
    }

    @Mapping(path = "/login", method = HttpMethod.POST)
    public void login(HttpRequest request, HttpResponse response) throws IOException {
        String inputUserId = request.getParameter(User.USER_ID);
        String inputPassword = request.getParameter(User.PASSWORD);
        if (inputUserId == null || inputPassword == null) {
            response.setStatusCode(StatusCode.UNAUTHORIZED);
            response.setLocation("/user/login_failed");
            return;
        }
        User userById = Database.findUserById(inputUserId);
        if (userById == null) {
            response.setStatusCode(StatusCode.UNAUTHORIZED);
            response.setLocation("/user/login_failed");
            return;
        }

        Cookie cookie = new Cookie();
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        String uuid = UUID.randomUUID().toString();
        cookie.setValue("SessionId", uuid);
        HttpSession.put(uuid, inputUserId);
        response.setCookie(cookie);
        response.setLocation("/");
    }

    @Mapping(path = "/user/login_failed", method = HttpMethod.GET)
    public void loginFailed(HttpRequest request, HttpResponse response) throws IOException {
        response.setStatusCode(StatusCode.UNAUTHORIZED);
        response.setHeader("Content-Type", "text/html; charset=utf-8");

        File file = new File("src/main/resources/static/login/login_failed.html");
        byte[] readFile = getFile(file);
        response.setBody(readFile);
    }

    @Mapping(path = "/logout", method = HttpMethod.POST)
    public void logout(HttpRequest request, HttpResponse response) throws IOException {

        Cookie cookie = request.getCookie();
        String sessionId = cookie.getCookie(HttpSession.SESSION_ID);
        if (sessionId != null){
            String userId = HttpSession.get(sessionId);
            if(userId != null){
                HttpSession.put(sessionId, null);
                Cookie newCookie = new Cookie();
                newCookie.setMaxAge(0);
                response.setCookie(newCookie);
            }
        }
        response.setLocation("/");
    }
}
