package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileReader;
import util.HttpMethod;
import util.RequestInfo;
import util.RequestParser;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

            String[] split = path.split("\\.");
            String extender = split[split.length - 1];

            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = FileReader.readFile(STATIC_FILE_PATH + path);

            response200Header(dos, body.length, extender);
            responseBody(dos, body);
            dos.flush();

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String extender) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            setContentTypeByFile(dos, extender);
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void setContentTypeByFile(DataOutputStream dos, String extender) throws IOException {
        System.out.println("extender = "+extender);
        switch (extender) {
            case "svg" -> dos.writeBytes("Content-Type: image/svg+xml\r\n");
            case "css" -> dos.writeBytes("Content-Type: text/css\r\n");
            case "html" -> dos.writeBytes("Content-Type: text/html\r\n");
            case "js" -> dos.writeBytes("Content-Type: application/javascript\r\n");
            case "ico" -> dos.writeBytes("Content-Type: image/x-icon\r\n");
            case "png" -> dos.writeBytes("Content-Type: image/png\r\n");
            case "jpg" -> dos.writeBytes("Content-Type: image/jpeg\r\n");
        }
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
