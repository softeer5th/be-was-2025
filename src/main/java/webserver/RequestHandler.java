package webserver;

import java.io.*;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             DataOutputStream dos = new DataOutputStream(connection.getOutputStream())) {
            HttpRequest request = new HttpRequest(reader);

            HttpResponse response = new HttpResponse();
            response.setProtocol(request.getProtocol());
            response.setStatusCode(StatusCode.OK);
            response.setHeader("Content-Type", "text/html");

            File file = new File("src/main/resources/static" + request.getUri());
            byte[] readFile = getFile(file);
            response.setBody(readFile);

            response.send(dos);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private static byte[] getFile(File file) throws IOException {
        try(FileInputStream fis = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];
            fis.read(bytes);
            return bytes;
        }
    }
}
