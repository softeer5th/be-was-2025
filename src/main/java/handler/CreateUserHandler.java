package handler;

import util.enums.HttpStatusCode;
import util.enums.Page;
import webserver.request.Request;
import util.UserManager;
import webserver.response.Response;
import webserver.response.ResponseWriter;

import java.io.DataOutputStream;
import java.io.IOException;

public class CreateUserHandler implements Handler{
    @Override
    public Response handle(Request request) {
        Response response = new Response(request, HttpStatusCode.SEE_OTHER);
        try {
            UserManager userManager = new UserManager();
            userManager.addUser(request.getBody());
            response.addHeader("Location", Page.MAIN_PAGE.getPath());
        } catch (IllegalArgumentException e) {
            response.addHeader("Location", Page.REGISTRATION.getPath());
        }
        return response;
    }
}
