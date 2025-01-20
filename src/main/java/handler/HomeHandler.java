package handler;

import http.HttpRequest;
import http.HttpResponse;
import http.constant.HttpStatus;
import util.FileUtils;
import util.SessionUtils;

import java.io.File;
import java.io.IOException;

public class HomeHandler implements Handler{

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if (SessionUtils.isLogin(httpRequest)) {
            httpResponse.redirect("/main");
            return;
        }
        String path = httpRequest.getPath().toLowerCase();


        File file = FileUtils.findFile(path);

        httpResponse.writeStatusLine(HttpStatus.OK);
        httpResponse.writeBody(file);
        httpResponse.send();
    }
}
