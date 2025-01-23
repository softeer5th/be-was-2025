package handler;

import util.enums.HttpStatusCode;
import util.enums.Page;
import webserver.request.Request;
import db.manage.UserManager;
import webserver.response.Response;

public class CreateUserHandler extends Handler{
    @Override
    public Response handle(Request request) {
        Response response = new Response(request, HttpStatusCode.SEE_OTHER);
        try {
            UserManager.addUser(request.getBody());
            response.addHeader("Location", Page.MAIN_PAGE.getPath());
        } catch (IllegalArgumentException e) {
            response.addHeader("Location", Page.REGISTRATION.getPath());
        }
        return response;
    }
}
