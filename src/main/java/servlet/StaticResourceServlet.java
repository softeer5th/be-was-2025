package servlet;

import webserver.httpserver.ContentType;
import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;
import webserver.httpserver.StatusCode;

import java.io.File;
import java.io.IOException;

import static utils.FileUtils.getFile;

/**
 * 정적 리소스를 서빙하는 서블릿
 */
public class StaticResourceServlet implements Servlet {
    @Override
    public boolean handle(HttpRequest request, HttpResponse response) throws IOException {
        response.setStatusCode(StatusCode.OK);
        response.setHeader("Content-Type", ContentType.guessContentType(request.getUri()).getMimeType());

        File file = new File("src/main/resources/static" + request.getUri());
        byte[] readFile = getFile(file);
        response.setBody(readFile);
        return true;
    }
}
