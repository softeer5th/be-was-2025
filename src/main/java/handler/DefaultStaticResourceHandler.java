package handler;

import http.HttpRequest;
import http.HttpResponse;
import http.constant.HttpStatus;
import util.FileUtils;

import java.io.File;
import java.io.IOException;

public class DefaultStaticResourceHandler implements Handler {
    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String path = httpRequest.getPath().toLowerCase();

        File file = FileUtils.findFile(path);

        httpResponse.writeStatusLine(HttpStatus.OK);
        httpResponse.writeBody(file);
        httpResponse.send();
    }
}
