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

            HttpResponse response = new HttpResponse();

            DataOutputStream dos = new DataOutputStream(out);
            response.setStatus(HttpStatus.OK);
            response.setContentType(extension);

            byte[] body = FileReader.readFile(STATIC_FILE_PATH + path)
                    .orElseThrow(() -> new FileNotFoundException(path));
            response.setBody(body);

            response.send(dos);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
