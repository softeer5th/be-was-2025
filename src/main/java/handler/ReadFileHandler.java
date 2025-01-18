package handler;

import util.FileFinder;
import util.HtmlContentReplacer;
import util.enums.HttpStatusCode;
import webserver.request.Request;
import webserver.response.Response;

import java.io.IOException;

public class ReadFileHandler extends Handler {

    @Override
    public Response handle(Request request) {
        Response response = new Response(request);
        try{
            FileFinder fileFinder = new FileFinder(request.url);
            byte[] body = null;
            if (fileFinder.find()) {
                body = fileFinder.readFileToBytes();
                if(request.isHtml()){
                    HtmlContentReplacer replacer = new HtmlContentReplacer(sessionId);
                    body = replacer.replace(body);
                }
            }
            response.setBody(body);
            response.setStatusCode(HttpStatusCode.OK);
        } catch(NullPointerException e){
            response.setStatusCode(HttpStatusCode.NOT_FOUND);
        } catch (IOException e) {
            response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR);
        }

        return response;
    }
}
