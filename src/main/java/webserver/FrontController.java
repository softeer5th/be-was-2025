package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.config.ServerConfig;
import webserver.enums.HttpVersion;
import webserver.exception.HttpException;
import webserver.handler.HttpHandler;
import webserver.request.HttpRequest;
import webserver.request.HttpRequestParser;
import webserver.response.HttpResponse;
import webserver.response.HttpResponseWriter;
import webserver.router.PathRouter;

import java.io.*;
import java.net.Socket;
import java.util.List;

// 클라이언트의 요청을 받아 HttpHandler에게 위임하는 Front-Controller
public class FrontController implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(FrontController.class);

    private final Socket connection;
    private final HttpRequestParser requestParser;
    private final HttpResponseWriter responseWriter;
    private final PathRouter router;

    private final List<HttpVersion> supportedHttpVersions;


    public FrontController(ServerConfig config, Socket connectionSocket, HttpRequestParser requestParser, HttpResponseWriter responseWriter, PathRouter router) {
        this.connection = connectionSocket;
        this.requestParser = requestParser;
        this.responseWriter = responseWriter;
        this.router = router;
        this.supportedHttpVersions = config.getSupportedHttpVersions();
    }

    public void run() {

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            HttpRequest request = null;
            try {
                request = requestParser.parse(reader);

                logger.debug("New Client: {}:{}, Request: {}", connection.getInetAddress(),
                        connection.getPort(), request);

                // request http version이 서버에서 지원하는지 검증
                request.validateSupportedHttpVersion(supportedHttpVersions);

                // request path에 해당하는 handler와 매칭된 path variable 찾기
                PathRouter.RoutingResult routingResult = router.route(request.getRequestTarget().getPath());

                HttpHandler handler = routingResult.handler();
                // requst에 매칭된 path variable 설정
                request.setPathVariables(routingResult.pathVariables());

                // handler에게 요청 위임
                HttpResponse response = handler.handle(request);

                responseWriter.writeResponse(request, response, out);
                logger.debug("Client:{}:{}, Response: {}", connection.getInetAddress(), connection.getPort(), response);

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


}
