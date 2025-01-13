package handler;

import model.HttpStatusCode;
import model.Page;
import util.RequestParser;
import util.UserManager;
import webserver.response.ResponseWriter;

import java.io.DataOutputStream;
import java.io.IOException;

public class CreateUserHandler implements Handler{
    @Override
    public void handle(DataOutputStream dos, RequestParser requestParser) throws IOException {
        ResponseWriter responseWriter = new ResponseWriter(dos, requestParser);
        try {
            UserManager userManager = new UserManager();
            userManager.addUser(requestParser.parameter);
            responseWriter.redirect(Page.MAIN_PAGE.getPath());
        } catch (IllegalArgumentException e) {
            responseWriter.redirect(Page.REGISTRATION.getPath());
        }
    }
}
