package webserver;

import handler.RequestRoute;
import handler.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import response.HttpResponse;
import request.RequestInfo;
import util.RequestParser;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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
            RequestInfo requestInfo = RequestParser.parse(in);
            String path = requestInfo.getPath();

            DataOutputStream dos = new DataOutputStream(out);

            Handler handler = RequestRoute.getHandler(path)
                    .orElseThrow(() -> new UnsupportedOperationException("No handler found for path" + path));


            HttpResponse response = handler.handle(requestInfo);
            response.send(dos);

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
