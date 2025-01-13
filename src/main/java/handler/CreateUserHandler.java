package handler;

import model.Page;
import util.RequestParser;
import util.UserManager;

import java.io.DataOutputStream;
import java.io.IOException;

public class CreateUserHandler implements Handler{
    @Override
    public void handle(DataOutputStream dos, RequestParser requestParser) throws IOException {
        try {
            UserManager userManager = new UserManager();
            userManager.addUser(requestParser.parameter);
            dos.writeBytes("HTTP/1.1 303 See Other \r\n");
            dos.writeBytes("Location: " + Page.MAIN_PAGE.getPath() + " \r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IllegalArgumentException e) {
            dos.writeBytes("HTTP/1.1 303 See Other \r\n");
            dos.writeBytes("Location: " + Page.REGISTRATION.getPath() + " \r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        }
    }
}
