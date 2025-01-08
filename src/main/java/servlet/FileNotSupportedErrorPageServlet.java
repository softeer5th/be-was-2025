package servlet;

import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;
import webserver.httpserver.StatusCode;

import java.io.File;
import java.io.IOException;

import static utils.FileUtils.getFile;

public class FileNotSupportedErrorPageServlet implements Servlet {
    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        response.setStatusCode(StatusCode.NOT_SUPPORTED);
        response.setHeader("Content-Type", "text/html");
        response.setBody("<h1> 406 Not Supported </h1>".getBytes());
        File file = new File("src/main/resources/static/error/notAcceptable.html");
        byte[] readFile = getFile(file);
        response.setBody(readFile);
    }
}
