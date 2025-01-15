package handler;

import util.enums.HttpStatusCode;
import webserver.request.Request;
import webserver.response.Response;
import webserver.response.ResponseWriter;

import java.io.DataOutputStream;
import java.io.IOException;

public class StaticFileHandler implements Handler {

    @Override
    public Response handle(Request request) {
        Response response = new Response(request);
        try{
            response.setBody();
            response.setStatusCode(HttpStatusCode.OK);
        } catch(NullPointerException e){
            response.setStatusCode(HttpStatusCode.NOT_FOUND);
        }

        return response;
    }
}
