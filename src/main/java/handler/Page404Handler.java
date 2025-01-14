package handler;

import util.enums.HttpStatusCode;
import webserver.request.Request;
import webserver.response.ResponseWriter;

import java.io.DataOutputStream;
import java.io.IOException;

public class Page404Handler implements Handler {
    @Override
    public void handle(DataOutputStream dos, Request request) throws IOException {
        ResponseWriter responseWriter = new ResponseWriter(dos, request);
        responseWriter.write(HttpStatusCode.NOT_FOUND);
    }
}
