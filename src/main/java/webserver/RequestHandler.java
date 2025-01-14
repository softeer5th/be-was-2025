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
            logger.error("Request handler run() : {} ", e.getMessage());
        }
    }

    private HttpRequestInfo requestParse(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.trim().isEmpty()) {
            throw new BaseException(HttpErrorCode.INVALID_HTTP_REQUEST);
        }

        String[] requestTokens = requestLine.replaceAll("\\s+", " ").trim().split(" ");
        if (requestTokens.length != 3) {
            throw new BaseException(HttpErrorCode.INVALID_HTTP_REQUEST);
        }

        HttpMethod httpMethod = HttpMethod.match(requestTokens[0].toLowerCase());
        String url = requestTokens[1];
        String body = parseRequestBody(reader);
        logger.debug("Request method = {}, url = {}", httpMethod, url);
        logger.debug("Request Body = {}", body);

        return new HttpRequestInfo(httpMethod, url, body);
    }

    public static String parseRequestBody(BufferedReader reader) throws IOException {
        String line;
        boolean isBody = false;
        int contentLength = 0;
        StringBuilder body = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) { // 헤더와 본문 사이의 빈 줄
                isBody = true;
                break;
            }
            if (line.toLowerCase().startsWith("content-length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        if (isBody && contentLength > 0) {
            char[] buffer = new char[contentLength];
            int read = reader.read(buffer, 0, contentLength);
            if (read > 0) {
                body.append(buffer, 0, read);
            }
            logger.debug("Body = {}", body);
        }
        return body.toString();
    }
}
