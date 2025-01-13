package handler;

import model.HttpStatusCode;
import util.FileFinder;
import util.RequestParser;

import java.io.DataOutputStream;
import java.io.IOException;

public class StaticFileHandler implements Handler {

    @Override
    public void handle(DataOutputStream dos, RequestParser requestParser) throws IOException {
        try{
            byte[] body = null;
            FileFinder fileFinder = new FileFinder(requestParser.url);
            if (fileFinder.find()) {
                body = fileFinder.readFileToBytes();
            }
            dos.writeBytes(HttpStatusCode.OK.getStartLine());
            dos.writeBytes("Content-Type: " + requestParser.contentType + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + body.length + "\r\n");
            dos.writeBytes("\r\n");
            dos.write(body, 0, body.length);
        } catch(NullPointerException e){
            dos.writeBytes(HttpStatusCode.NOT_FOUND.getStartLine());
        } catch (IOException e){
            dos.writeBytes(HttpStatusCode.INTERNAL_SERVER_ERROR.getStartLine());
        } finally {
            dos.flush();
        }
    }
}
