package webserver;

import java.io.*;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileUtils;
import util.MimeType;
import util.RequestParser;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    private final RequestParser requestParser;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
        this.requestParser = new RequestParser();
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            DataOutputStream dos = new DataOutputStream(out);

            requestParser.parse(in);

            String target = requestParser.getTarget();

            File file = FileUtils.findFile(target);

            byte[] body = createBody(file);

            String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);

            String mimeType = MimeType.valueOf(extension.toUpperCase()).getMimeType();

            response200Header(dos, body.length, mimeType);
            responseBody(dos, body);
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String mimetype) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes(String.format("Content-Type: %s;charset=utf-8\r\n", mimetype));
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

    private byte[] createBody(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        byte[] body = is.readAllBytes();
        is.close();

        return body;
    }
}
