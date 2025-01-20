package handler;

import db.Database;
import http.HttpRequest;
import http.HttpResponse;
import http.constant.HttpStatus;
import model.Session;
import model.User;
import util.DynamicHtmlEditor;
import util.FileUtils;
import util.MimeType;
import util.SessionUtils;

import java.io.File;
import java.io.IOException;

public class MainHandler implements Handler{

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if (!SessionUtils.isLogin(httpRequest)) {
            httpResponse.redirect("/");
            return;
        }
        String path = httpRequest.getPath().toLowerCase();

        File file = FileUtils.findFile(path);
        String content = FileUtils.convertToString(file);
        String extension = FileUtils.getExtension(file);
        MimeType mimeType = MimeType.valueOf(extension.toUpperCase());


        Session session = SessionUtils.findSession(httpRequest);
        User user = Database.findUserById(session.userId())
                .orElseThrow(() -> new RuntimeException("user not found"));
        content = DynamicHtmlEditor.edit(content, "username", user.getName());


        byte[] body = content.getBytes();
        httpResponse.writeStatusLine(HttpStatus.OK);
        httpResponse.writeBody(body, mimeType.getMimeType());
        httpResponse.send();
    }
}
