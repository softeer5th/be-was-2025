package webserver;

import java.io.*;
import java.net.Socket;

import model.RequestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final StaticResourceLoader resourceLoader = new StaticResourceLoader("src/main/resources/static");
    private final HttpRequestParser requestParser = new HttpRequestParser();
    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    @Override
    public void run() {
        logger.debug("New Client Connect! IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            RequestData requestData = requestParser.parse(in);
            byte[] responseBody = resourceLoader.load(requestData.path());

            HttpResponse response = new HttpResponse(new DataOutputStream(out));
            if (responseBody == null) {
                byte[] notFoundBody = "<h1>404 File Not Found</h1>".getBytes();
                response.send404(notFoundBody);
            } else {
                response.send200(responseBody, requestData.path());
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}