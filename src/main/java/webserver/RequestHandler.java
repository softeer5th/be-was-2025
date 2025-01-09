package webserver;

import java.io.*;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.*;
import util.exception.InvalidRequestLineSyntaxException;

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

            HttpResponseHandler.responseHeader(dos, body.length, mimeType, HttpStatus.OK);
            HttpResponseHandler.responseBody(dos, body);
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (InvalidRequestLineSyntaxException e) {
            logger.error(e.getMessage());
            try (OutputStream out = connection.getOutputStream()) {
                DataOutputStream dos = new DataOutputStream(out);
                byte[] body = e.getMessage().getBytes();
                HttpResponseHandler.responseHeader(dos, body.length, "text/plain", e.httpStatus);
                HttpResponseHandler.responseBody(dos, body);
            } catch (IOException ex) {
                logger.error(ex.getMessage());
            }
        }
    }

    private byte[] createBody(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        byte[] body = is.readAllBytes();
        is.close();

        return body;
    }
}
