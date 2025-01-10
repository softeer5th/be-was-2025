package handler;

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
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + requestParser.contentType + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + body.length + "\r\n");
            dos.writeBytes("\r\n");
            dos.write(body, 0, body.length);
            dos.flush();
        } catch(NullPointerException e){
            dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
        } catch (IOException e){
            dos.writeBytes("HTTP/1.1 500 Internal Server Error\r\n");
        }
    }
}
