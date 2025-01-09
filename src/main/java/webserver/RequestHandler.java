package webserver;

import java.io.*;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.HttpStatus;
import webserver.http.MimeType;
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
            HttpResponse response = new HttpResponse(request, dos);
            logger.debug("Request: {}", request);
            if (requestStaticResource(request)) {
                serveStaticResource(request, response);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void serveStaticResource(HttpRequest request, HttpResponse response) throws IOException {
        File file = ResourceResolver.getResource(request.getUrl());
        response.setBody(file);
        response.setContentType(MimeType.getMimeType(file.getName()));
        response.setContentLength(file.length());
        response.setStatus(HttpStatus.OK);
        response.send();
    }

    private boolean requestStaticResource(HttpRequest request) throws IOException {
        if(!"GET".equals(request.getMethod())) return false;

        File file = ResourceResolver.getResource(request.getUrl());
        return file.exists() && file.isFile();
    }
}
