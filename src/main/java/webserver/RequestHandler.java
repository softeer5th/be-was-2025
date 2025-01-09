package webserver;

import java.io.*;
import java.net.Socket;

import db.Database;
import model.Mime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileFinder;
import util.RequestParser;
import util.UserManeger;

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
            String url = requestParser.url;
            if(url.equals("/user/create")){
                UserManeger userManeger = new UserManeger();
                try {
                    userManeger.addUser(requestParser.parameter);
                } catch (IllegalArgumentException e) {
                    logger.error(e.getMessage());
                    response303Header(dos, "/registration");
                }
                response303Header(dos, "/login");
            }
            else {
                byte[] body = makeBody(url);
                response200Header(dos, body.length, contentType);
                responseBody(dos, body);
            }
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

    private void response303Header(DataOutputStream dos, String url) throws IOException {
        try {
            dos.writeBytes("HTTP/1.1 303 See Other \r\n");
            dos.writeBytes("Location: " + url + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
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

    private byte[] makeBody(String url){
        byte[] body = null;
        try {
            FileFinder fileFinder = new FileFinder(url);
            if (fileFinder.find()) {
                body = fileFinder.readFileToBytes();
            }
        }
        catch (IOException e) {
            logger.error(e.getMessage());
        }
        return body;
    }
}
