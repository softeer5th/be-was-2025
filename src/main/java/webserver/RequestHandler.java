package webserver;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import http.HttpMethod;
import http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ParsingUtil;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private Socket connection;
    private RequestRouter requestRouter;

    public RequestHandler(Socket connectionSocket, RequestRouter requestRouter) {
        this.connection = connectionSocket;
        this.requestRouter = requestRouter;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            DataOutputStream dos = new DataOutputStream(out);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            handleRequest(br, dos);
        } catch (Exception e) {
            logger.debug(Arrays.toString(e.getStackTrace()));
        }
    }

    private void handleRequest(BufferedReader br, DataOutputStream dos) {
        try {
            List<String> headerLines = ParsingUtil.parseRequestHeader(br);
            HttpRequest httpRequest = new HttpRequest(headerLines);
            if (httpRequest.getHttpMethod() == HttpMethod.POST) {
                int contentLength = httpRequest.getContentLength();
                char[] requestBody = new char[contentLength];
                br.read(requestBody, 0, contentLength);
                httpRequest.setBody(new String(requestBody));
            }
            httpRequest.log(logger);
            requestRouter.route(httpRequest, dos);
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }
}
