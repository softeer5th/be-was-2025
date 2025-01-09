package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import http.*;
import http.enums.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileUtil;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final WebServlet webServlet = WebServlet.getInstance();

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream();OutputStream out = connection.getOutputStream()) {
            webServlet.process(in, out);
        }
        catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

}
