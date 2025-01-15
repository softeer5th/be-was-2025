package handler;

import util.enums.HttpStatusCode;
import webserver.request.Request;
import webserver.response.Response;
import webserver.response.ResponseWriter;

import java.io.DataOutputStream;
import java.io.IOException;

public class Page404Handler implements Handler {
    @Override
    public Response handle(Request request) {
        return new Response(request, HttpStatusCode.NOT_FOUND);
    }
}
