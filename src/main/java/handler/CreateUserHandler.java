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
        ResponseWriter responseWriter = new ResponseWriter(dos, request);
        try {
            UserManager userManager = new UserManager();
            userManager.addUser(request.parameter);
            responseWriter.redirect(Page.MAIN_PAGE.getPath());
        } catch (IllegalArgumentException e) {
            responseWriter.redirect(Page.REGISTRATION.getPath());
        }
    }
}
