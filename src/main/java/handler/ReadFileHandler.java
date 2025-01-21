package handler;

import util.FileFinder;
import util.HtmlContentReplacer;
import util.enums.HttpStatusCode;
import util.enums.Page;
import webserver.request.Request;
import webserver.response.Response;
import webserver.session.SessionManager;

import java.io.IOException;

public class ReadFileHandler extends Handler {

    @Override
    public Response handle(Request request) {
        Response response = new Response(request);
        sessionId = SessionManager.validate(sessionId);

        if(sessionId == null && Page.isRequireLogin(request.url)){
            response.setStatusCode(HttpStatusCode.SEE_OTHER);
            response.addHeader("Location", Page.LOGIN.getPath());
            return response;
        }

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
            System.out.println(e.getMessage());
            response.setStatusCode(HttpStatusCode.NOT_FOUND);
        } catch (IOException e) {
            response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR);
        }

        return response;
    }
}
