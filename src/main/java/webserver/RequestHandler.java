package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileUtil;
import webserver.config.ServerConfig;
import webserver.enums.HttpStatusCode;
import webserver.enums.HttpVersion;
import webserver.exception.HttpException;
import webserver.exception.NotImplemented;
import webserver.file.StaticResourceManager;
import webserver.request.HttpRequest;
import webserver.request.HttpRequestParser;
import webserver.response.HttpResponse;
import webserver.response.HttpResponseWriter;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Optional;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final HttpRequestParser requestParser;
    private final HttpResponseWriter responseWriter;
    private final StaticResourceManager resourceManager;
    private final List<HttpVersion> supportedHttpVersions;
    private final String defaultPageFileName;


    public RequestHandler(Socket connectionSocket, HttpRequestParser requestParser, HttpResponseWriter responseWriter, ServerConfig config, StaticResourceManager resourceManager) {
        this.connection = connectionSocket;
        this.requestParser = requestParser;
        this.responseWriter = responseWriter;
        this.resourceManager = resourceManager;
        this.supportedHttpVersions = config.getSupportedHttpVersions();
        this.defaultPageFileName = config.getDefaultPageFileName();
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
                request.validateSupportedHttpVersion(supportedHttpVersions);


                // Http Method에 따라 로직 분기(processXXX 메서드)
                HttpResponse response = switch (request.getMethod()) {
                    case GET -> processGet(request);
                    default -> throw new NotImplemented("Unsupported Method " + request.getMethod());
                };

                responseWriter.writeResponse(request, response, out);
            } catch (HttpException e) {
                logger.debug(e.getMessage());
                // 에러 응답
                // HTTP 버전이 설정되어 있지 않으면 기본값으로 응답
                HttpVersion version = request != null ? request.getVersion() : supportedHttpVersions.get(0);
                responseWriter.writeError(version, e, out);
            }

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private HttpResponse processGet(HttpRequest request) {
        String requestTarget = request.getRequestTarget();
        // 디렉토리일 경우 디렉토리 내의 default page 파일로 응답
        if (resourceManager.isDirectory(requestTarget))
            requestTarget = FileUtil.joinPath(requestTarget, defaultPageFileName);

        Optional<File> file = resourceManager.getFile(requestTarget);
        return file
                .map(f -> new HttpResponse(HttpStatusCode.OK).setBody(f))
                .orElseGet(() -> new HttpResponse(HttpStatusCode.NOT_FOUND));
    }

}
