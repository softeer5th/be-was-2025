package controller;

import wasframework.HttpSession;
import wasframework.Mapping;
import webserver.httpserver.HttpMethod;
import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;
import webserver.httpserver.StatusCode;
import webserver.httpserver.header.Cookie;

import java.io.File;
import java.io.IOException;

import static utils.FileUtils.getFile;

public class HomeController {

    @Mapping(path = "/", method = HttpMethod.GET)
    public void home(HttpRequest request, HttpResponse response) throws IOException {
        response.setStatusCode(StatusCode.OK);
        response.setHeader("Content-Type", "text/html; charset=utf-8");

        Cookie cookie = request.getCookie();
        String sessionId = cookie.getCookie("SessionId");
        byte[] readFile;
        if (sessionId != null && HttpSession.get(sessionId) != null) {
            File file = new File("src/main/resources/static/main/index.html");
            readFile = getFile(file);
            response.setBody(readFile);
            return;
        }
        File file = new File("src/main/resources/static/index.html");
        readFile = getFile(file);
        response.setBody(readFile);
    }
}
