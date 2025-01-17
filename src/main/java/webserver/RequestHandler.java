package webserver;

import http.HttpRequestInfo;
import exception.BaseException;
import handler.Handler;

import java.io.*;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import router.Router;
import http.HttpResponse;

public class RequestHandler implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final Router router;

    public RequestHandler(Socket connectionSocket, Router router) {
        this.connection = connectionSocket;
        this.router = router;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}",
                connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequestInfo httpRequestInfo = new HttpRequestInfo(in);
            DataOutputStream dos = new DataOutputStream(out);

            HttpResponse response;

            try {
                final Handler handler = router.route(httpRequestInfo.getPath());
                logger.debug("Url = {}", httpRequestInfo.getPath());

                response = handler.handle(httpRequestInfo);
            } catch (BaseException e) {
                response = new HttpResponse(e.getStatus(), "text/html; charset=utf-8", e.getMessage());
                logger.error(e.getMessage());
            }

            response.send(dos);
        } catch (IOException e) {
            logger.error("Request handler run() : {} ", e.getMessage());
        }
    }
}