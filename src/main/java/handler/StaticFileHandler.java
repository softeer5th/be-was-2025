package handler;

import util.enums.HttpStatusCode;
import webserver.request.Request;
import webserver.response.Response;

public class StaticFileHandler extends Handler {

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
