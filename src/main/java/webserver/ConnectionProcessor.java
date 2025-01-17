package webserver;

import java.io.*;
import java.net.Socket;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.enums.CookieName;
import webserver.request.Request;
import webserver.response.ResponseBuilder;
import webserver.request.RequestParser;
import webserver.session.SessionManager;

public class ConnectionProcessor implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionProcessor.class);
    private static final ResponseBuilder responseBuilder = new ResponseBuilder();

    private Socket connection;

    public ConnectionProcessor(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            Request request = RequestParser.parse(in);
            getLogs(request);

            DataOutputStream dos = new DataOutputStream(out);

            String sid = request.cookie.getValue(CookieName.SESSION_COOKIE.getName());
            responseBuilder.buildResponse(dos, request, SessionManager.validate(sid));

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
    private void getLogs(Request request) {
        logger.debug("request: ");
        logger.debug(request.getRequestLine());
        for(Map.Entry<String, String> header : request.getHeaders().entrySet()) {
            String line = header.getKey() + ": " + header.getValue();
            logger.debug(line);
        }
        if(request.getBody() != null) {
            logger.debug(request.getBody());
        }
    }
}
