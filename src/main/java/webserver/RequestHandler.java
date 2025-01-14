package webserver;

import http.handler.Handler;
import http.request.HttpRequest;
import http.request.HttpRequestParser;
import http.response.HttpResponse;
import http.router.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private static final String RESOURCE_PATH = "./src/main/resources/static";

    private Socket connection;

    private static final Router router = new Router();

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            HttpRequest httpRequest = HttpRequestParser.parseRequest(in);
            HttpResponse httpResponse = new HttpResponse(out);

            Handler httpRequestHandler = router.route(httpRequest);
            httpRequestHandler.handle(httpRequest, httpResponse);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
