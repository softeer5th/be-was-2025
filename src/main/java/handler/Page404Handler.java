package handler;

import util.enums.HttpStatusCode;
import webserver.request.Request;
import webserver.response.Response;
import webserver.response.ResponseWriter;

import java.io.DataOutputStream;
import java.io.IOException;

public class Page404Handler implements Handler {
    @Override
    public void handle(DataOutputStream dos, Request request) throws IOException {
        Response response = new Response(request, HttpStatusCode.NOT_FOUND);
        ResponseWriter.write(dos, response);
    }
}
