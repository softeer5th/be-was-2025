package webserver;

import java.io.*;
import java.net.Socket;

import model.Mime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileFinder;
import util.RequestParser;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            RequestParser requestParser = new RequestParser(in);
            requestParser.getLogs(logger);

            DataOutputStream dos = new DataOutputStream(out);
            String contentType = Mime.getByExtension(requestParser.extension).getContentType();
            byte[] body = makeBody(requestParser.url);

            response200Header(dos, body.length, contentType);
            responseBody(dos, body);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) throws IOException {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private byte[] readFileToBytes(String path){
        File file = new File(path);
        byte[] bytes = new byte[(int) file.length()];

        try(FileInputStream fis = new FileInputStream(file)) {
            fis.read(bytes);
        }
        catch (IOException e) {
            logger.error(e.getMessage());
        }
        return bytes;
    }

    private byte[] makeBody(String url){
        FileFinder fileFinder = new FileFinder(url);
        if(fileFinder.find()){
            return readFileToBytes(fileFinder.getPath());
        }
        else return null;
    }
}
