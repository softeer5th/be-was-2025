package webserver.servlet;

import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;
import webserver.httpserver.StatusCode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class StaticResourceServlet implements Servlet {
    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {

        response.setProtocol(request.getProtocol());
        response.setStatusCode(StatusCode.OK);
        response.setHeader("Content-Type", request.guessContentType());

        File file = new File("src/main/resources/static" + request.getUri());
        byte[] readFile = getFile(file);
        response.setBody(readFile);

    }
    private byte[] getFile(File file) throws IOException {
        try(FileInputStream fis = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];
            fis.read(bytes);
            return bytes;
        }
    }
}
