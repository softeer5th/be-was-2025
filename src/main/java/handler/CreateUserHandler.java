package handler;

import util.enums.Page;
import webserver.request.Request;
import util.UserManager;
import webserver.response.ResponseWriter;

import java.io.DataOutputStream;
import java.io.IOException;

public class CreateUserHandler implements Handler{
    @Override
    public void handle(DataOutputStream dos, Request request) throws IOException {
        try {
            UserManager userManager = new UserManager();
            userManager.addUser(request.getBody());
            ResponseWriter.redirect(dos, Page.MAIN_PAGE.getPath());
        } catch (IllegalArgumentException e) {
            ResponseWriter.redirect(dos, Page.REGISTRATION.getPath());
        }
    }
}
