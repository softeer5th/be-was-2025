package handler;

import util.enums.HttpStatusCode;
import webserver.request.RequestParser;
import webserver.response.ResponseWriter;

import java.io.DataOutputStream;
import java.io.IOException;

public class StaticFileHandler implements Handler {

    @Override
    public void handle(DataOutputStream dos, RequestParser requestParser) throws IOException {
        ResponseWriter responseWriter = new ResponseWriter(dos, requestParser);
        try{
            responseWriter.write(HttpStatusCode.OK);
        } catch(NullPointerException e){
            responseWriter.write(HttpStatusCode.NOT_FOUND);
        } catch (IOException e){
            responseWriter.write(HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
    }
}
