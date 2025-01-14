package webserver;

import http.HttpMethod;
import http.HttpRequestInfo;
import exception.BaseException;
import exception.HttpErrorCode;
import handler.Handler;
import java.io.*;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import router.Router;
import http.HttpResponse;

public class RequestHandler implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
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
            HttpRequestInfo httpRequestInfo = requestParse(in);
            DataOutputStream dos = new DataOutputStream(out);

            HttpResponse response;

            try {
                final Handler handler = router.route(httpRequestInfo.getPath());
                logger.debug("Url = " + httpRequestInfo.getPath());

                response = handler.handle(httpRequestInfo);
            } catch (BaseException e) {
                response = new HttpResponse(e.getStatus(), "text/html; charset=utf-8", e.getMessage());
                logger.error(e.getMessage());
            }

            response.send(dos);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private HttpRequestInfo requestParse(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.trim().isEmpty()) {
            logger.error("Request line is empty");
            throw new BaseException(HttpErrorCode.INVALID_HTTP_REQUEST);
        }

        String[] requestTokens = requestLine.replaceAll("\\s+", " ").trim().split(" ");
        if (requestTokens.length != 3) {
            logger.error("Request token length is not 3");
            throw new BaseException(HttpErrorCode.INVALID_HTTP_REQUEST);
        }

        HttpMethod httpMethod = HttpMethod.match(requestTokens[0].toLowerCase());
        String url = requestTokens[1];
        logger.debug("Request mehtod = {}, url = {}", httpMethod, url);

        return new HttpRequestInfo(httpMethod, url);
    }
}
