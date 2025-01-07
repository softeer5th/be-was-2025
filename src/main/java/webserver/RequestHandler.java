package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.*;
import util.FileReader;

import java.io.*;
import java.net.Socket;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final String STATIC_FILE_PATH = "src/main/resources/static";

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            RequestInfo requestInfo = RequestParser.parse(in);

            HttpMethod method = requestInfo.getMethod();
            String path = requestInfo.getPath();

            FileContentType extension = FileContentType.getExtensionFromPath(path);

            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = FileReader.readFile(STATIC_FILE_PATH + path)
                    .orElseThrow(() -> new FileNotFoundException(path));

            response200Header(dos, body.length, extension);
            responseBody(dos, body);
            dos.flush();


            // dynamic 요청
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, FileContentType extension) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            setContentTypeByFile(dos, extension);
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void setContentTypeByFile(DataOutputStream dos, FileContentType extension) throws IOException {
        System.out.println("extension = " + extension);

        dos.writeBytes("Content-Type: " + extension.getContentType() + "\r\n");
    }

    private void response404Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 404 Not Found \r\n");
            dos.writeBytes("Content-Type: text/html\r\n");
            dos.writeBytes("Connection: close\r\n");
            dos.writeBytes("\r\n");

            dos.writeBytes("<html><body><h1>404 Not Found</h1></body></html>");

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
}
