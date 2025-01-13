package handler;

import model.HttpStatusCode;
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
            dos.writeBytes(HttpStatusCode.SEE_OTHER.getStartLine());
            dos.writeBytes("Location: " + Page.MAIN_PAGE.getPath() + " \r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IllegalArgumentException e) {
            dos.writeBytes(HttpStatusCode.SEE_OTHER.getStartLine());
            dos.writeBytes("Location: " + Page.REGISTRATION.getPath() + " \r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        }
    }
}
