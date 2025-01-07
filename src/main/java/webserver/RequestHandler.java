package webserver;

import java.io.*;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.support.ResourceResolver;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            DataOutputStream dos = new DataOutputStream(out);
            HttpRequest request = new HttpRequest(in);
            logger.debug("Request: {}", request);
            File file = ResourceResolver.getResource(request.getUrl());
            if (file.exists() && file.isFile()) {
                HttpResponse response = new HttpResponse(file, dos);
                response.send();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
