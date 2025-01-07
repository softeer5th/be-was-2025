package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileUtil;
import webserver.config.ServerConfig;
import webserver.enums.HttpStatusCode;
import webserver.request.HttpRequest;
import webserver.request.HttpRequestParser;
import webserver.response.HttpResponse;
import webserver.response.HttpResponseWriter;

import java.io.*;
import java.net.Socket;
import java.util.Optional;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final HttpRequestParser requestParser;
    private final HttpResponseWriter responseWriter;
    private final ServerConfig config;

    public RequestHandler(Socket connectionSocket, HttpRequestParser requestParser, HttpResponseWriter responseWriter, ServerConfig config) {
        this.connection = connectionSocket;
        this.requestParser = requestParser;
        this.responseWriter = responseWriter;
        this.config = config;
    }

    public void run() {

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            HttpRequest request = requestParser.parse(reader);

            logger.debug("New Client Connect! Connected IP : {}, Port : {}, Request: {}", connection.getInetAddress(),
                    connection.getPort(), request);

            // request http version이 서버에서 지원하는지 검증
            request.validateSupportedHttpVersion(config.getSupportedHttpVersions());


            // Http Method에 따라 로직 분기(processXXX 메서드)
            HttpResponse response = switch (request.getMethod()) {
                case GET -> processGet(request);
                default -> throw new IllegalStateException("Unsupported Method " + request.getMethod());
            };

            responseWriter.write(request, response, out);

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private HttpResponse processGet(HttpRequest request) {
        String requestTarget = request.getRequestTarget();
        Optional<File> file = FileUtil.getFileInResources(requestTarget);
        return file.map(f -> new HttpResponse(HttpStatusCode.OK).setBody(f))
                .orElseGet(() -> new HttpResponse(HttpStatusCode.NOT_FOUND));
    }

}
