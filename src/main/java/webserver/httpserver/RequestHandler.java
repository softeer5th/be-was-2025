package webserver.httpserver;

import java.io.*;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlet.ServletManager;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    private ServletManager servletManager;

    public RequestHandler(Socket connectionSocket, ServletManager servletManager) {
        this.connection = connectionSocket;
        this.servletManager = servletManager;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
             DataOutputStream dos = new DataOutputStream(connection.getOutputStream())) {
            servletManager.serve(bis, dos);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
