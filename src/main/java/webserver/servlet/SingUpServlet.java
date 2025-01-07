package webserver.servlet;

import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;
import webserver.httpserver.StatusCode;

import java.io.File;
import java.io.IOException;

import static utils.FileUtils.getFile;

public class SingUpServlet implements Servlet {
    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        response.setStatusCode(StatusCode.OK);
        response.setHeader("Content-Type", "text/html");

        File file = new File("src/main/resources/static/registration/index.html");
        byte[] readFile = getFile(file);
        response.setBody(readFile);
    }
}
