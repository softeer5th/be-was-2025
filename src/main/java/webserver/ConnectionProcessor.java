package webserver;

import java.io.*;
import java.net.Socket;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.request.Request;
import util.ResponseBuilder;

public class ConnectionProcessor implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionProcessor.class);
    private final ResponseBuilder responseBuilder = new ResponseBuilder();

    private Socket connection;

    public ConnectionProcessor(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            Request request = new Request(in);
            getLogs(request.getRequests());

            DataOutputStream dos = new DataOutputStream(out);

            responseBuilder.buildResponse(dos, request);

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
    private void getLogs(List<String> requests) {
        logger.debug("request: ");
        for(String request : requests){
            logger.debug(request);
        }
    }
}
