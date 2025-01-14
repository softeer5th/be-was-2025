package webserver;

import java.io.*;
import java.net.Socket;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.request.RequestParser;
import util.ResponseBuilder;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final ResponseBuilder responseBuilder = new ResponseBuilder();

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            RequestParser requestParser = new RequestParser(in);
            getLogs(requestParser.getRequests());

            DataOutputStream dos = new DataOutputStream(out);

            responseBuilder.buildResponse(dos, requestParser);

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
