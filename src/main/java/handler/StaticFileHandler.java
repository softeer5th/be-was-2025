package handler;

import util.enums.HttpStatusCode;
import webserver.request.Request;
import webserver.response.Response;
import webserver.response.ResponseWriter;

import java.io.DataOutputStream;
import java.io.IOException;

public class StaticFileHandler implements Handler {

    @Override
    public void handle(DataOutputStream dos, Request request) throws IOException {
        Response response = new Response(request);
        try{
            response.setBody();
            response.setStatusCode(HttpStatusCode.OK);
            ResponseWriter.write(dos, response);
        } catch(NullPointerException e){
            response.setStatusCode(HttpStatusCode.NOT_FOUND);
            ResponseWriter.write(dos, response);
        } catch (IOException e){
            response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR);
            ResponseWriter.write(dos, response);
        }
    }
}
