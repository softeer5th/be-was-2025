package webserver.httpserver;

import java.io.*;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.servlet.ServletManager;

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

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             DataOutputStream dos = new DataOutputStream(connection.getOutputStream())) {
            HttpRequest request = new HttpRequest(reader);
            HttpResponse response = new HttpResponse();
            response.setProtocol(request.getProtocol());

            servletManager.serve(request, response);
            response.send(dos);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
