package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileUtil;
import webserver.config.ServerConfig;
import webserver.enums.HttpStatusCode;
import webserver.enums.HttpVersion;
import webserver.exception.HttpException;
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
    private final FileUtil fileUtil;

    public RequestHandler(Socket connectionSocket, HttpRequestParser requestParser, HttpResponseWriter responseWriter, ServerConfig config, FileUtil fileUtil) {
        this.connection = connectionSocket;
        this.requestParser = requestParser;
        this.responseWriter = responseWriter;
        this.config = config;
        this.fileUtil = fileUtil;
    }

    public void run() {

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            HttpRequest request = null;
            try {
                request = requestParser.parse(reader);

                logger.debug("New Client Connect! Connected IP : {}, Port : {}, Request: {}", connection.getInetAddress(),
                        connection.getPort(), request);

                // request http version이 서버에서 지원하는지 검증
                request.validateSupportedHttpVersion(config.getSupportedHttpVersions());


                // Http Method에 따라 로직 분기(processXXX 메서드)
                HttpResponse response = switch (request.getMethod()) {
                    case GET -> processGet(request);
                    default -> throw new IllegalStateException("Unsupported Method " + request.getMethod());
                };

                responseWriter.writeResponse(request, response, out);
            } catch (HttpException e) {
                logger.debug(e.getMessage());
                // 에러 응답
                HttpVersion version = request != null ? request.getVersion() : config.getSupportedHttpVersions().get(0);
                responseWriter.writeError(version, e, out);
            }

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private HttpResponse processGet(HttpRequest request) {
        String requestTarget = request.getRequestTarget();
        Optional<File> file = fileUtil.getFileInResources(requestTarget);
        return file.map(f -> new HttpResponse(HttpStatusCode.OK).setBody(f))
                .orElseGet(() -> new HttpResponse(HttpStatusCode.NOT_FOUND));
    }

}
