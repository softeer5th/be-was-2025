package webserver;

import exception.ClientErrorException;
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

import static exception.ErrorCode.*;

public class RequestDispatcher implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestDispatcher.class);
    private Socket connection;

    public RequestDispatcher(Socket connectionSocket) {
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
                    .orElseThrow(() -> new ClientErrorException(NOT_ALLOWED_PATH));


            HttpResponse response = handler.handle(requestInfo);
            response.send(dos);

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
