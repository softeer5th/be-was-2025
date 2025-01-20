package handler;

import util.enums.HttpStatusCode;
import webserver.request.Request;
import webserver.response.Response;

public class Page404Handler extends Handler {
    @Override
    public Response handle(Request request) {
        return new Response(request, HttpStatusCode.NOT_FOUND);
    }
}
