package webserver;

import java.io.*;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);
    private final WebServlet webServlet = WebServlet.getInstance();

    private Socket connection;

    public ConnectionHandler(Socket connectionSocket) {
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
