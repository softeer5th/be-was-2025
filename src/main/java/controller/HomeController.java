package controller;

import wasframework.Mapping;
import webserver.httpserver.HttpMethod;
import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;
import webserver.httpserver.StatusCode;

import java.io.File;
import java.io.IOException;

import static utils.FileUtils.getFile;

public class HomeController {

    @Mapping(path = "/", method = HttpMethod.GET)
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        response.setStatusCode(StatusCode.OK);
        response.setHeader("Content-Type", "text/html; charset=utf-8");

        File file = new File("src/main/resources/static/index.html");
        byte[] readFile = getFile(file);
        response.setBody(readFile);
    }
}
