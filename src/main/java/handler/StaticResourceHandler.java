package handler;

import http.HttpRequest;
import http.HttpResponse;
import http.constant.HttpStatus;
import util.*;

import java.io.File;
import java.io.IOException;

public class StaticResourceHandler {
    private final HttpRequest httpRequest;
    private final HttpResponse httpResponse;

    public StaticResourceHandler(HttpRequest httpRequest, HttpResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
    }

    public void handleStaticResource() throws IOException {
        String path = httpRequest.getPath().toLowerCase();

        Handler handler = StaticResourcePathPool.getInstance().getHandler(path);

        if (handler != null) {
            handler.handle(httpRequest, httpResponse);
            return;
        }

        File file = FileUtils.findFile(path);
        httpResponse.writeStatusLine(HttpStatus.OK);
        httpResponse.writeBody(file);
        httpResponse.send();
    }
}
