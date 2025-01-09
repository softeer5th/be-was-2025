package webserver;

import enums.FileContentType;
import exception.ClientErrorException;
import handler.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.RequestInfo;
import response.HttpResponse;
import router.Router;
import util.HttpRequestParser;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RequestDispatcher implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestDispatcher.class);
    private Socket connection;
    private final Router router;

    public RequestDispatcher(Socket connectionSocket,Router router) {
        this.connection = connectionSocket;
        this.router = router;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            RequestInfo requestInfo = HttpRequestParser.parse(in);
            String path = requestInfo.getPath();

            DataOutputStream dos = new DataOutputStream(out);
            HttpResponse response;

            try {
                final Handler handler = router.route(path);
                response = handler.handle(requestInfo);
            } catch (ClientErrorException e) {
                response = new HttpResponse(e.getHttpStatus(), FileContentType.HTML_UTF_8, e.getMessage());
            }

            response.send(dos);


        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
