package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.config.ServerConfig;
import webserver.enums.HttpVersion;
import webserver.exception.HttpException;
import webserver.exception.filter.ExceptionFilterChain;
import webserver.handler.HttpHandler;
import webserver.interceptor.InterceptorChain;
import webserver.request.HttpRequest;
import webserver.request.HttpRequestParser;
import webserver.response.HttpResponse;
import webserver.response.HttpResponseWriter;
import webserver.router.PathRouter;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import static util.CommonUtil.close;

/**
 * 클라이언트의 요청을 받아 HttpHandler에게 위임하는 Front-Controller
 */
public class FrontController implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(FrontController.class);

    private final Socket connection;
    private final HttpRequestParser requestParser;
    private final HttpResponseWriter responseWriter;
    private final PathRouter router;

    private final List<HttpVersion> supportedHttpVersions;
    private final InterceptorChain interceptorChain;
    private final ExceptionFilterChain exceptionFilterChain;

    /**
     * FrontController 생성자
     *
     * @param config               서버 설정
     * @param connectionSocket     클라이언트와 연결된 소켓
     * @param requestParser        http request 문자열 파서
     * @param responseWriter       http response 문자열 작성기
     * @param router               path에 따른 handler를 매칭하는 router
     * @param interceptorChain     interceptor chain
     * @param exceptionFilterChain exception filter chain
     */
    public FrontController(ServerConfig config, Socket connectionSocket, HttpRequestParser requestParser, HttpResponseWriter responseWriter, PathRouter router, InterceptorChain interceptorChain, ExceptionFilterChain exceptionFilterChain) {
        this.connection = connectionSocket;
        this.requestParser = requestParser;
        this.responseWriter = responseWriter;
        this.router = router;
        this.supportedHttpVersions = config.getSupportedHttpVersions();
        this.interceptorChain = interceptorChain;
        this.exceptionFilterChain = exceptionFilterChain;
    }

    /**
     * 클라이언트 요청을 받아 처리하는 메소드
     */
    public void run() {

        try (InputStream in = new BufferedInputStream(connection.getInputStream()); OutputStream out = connection.getOutputStream()) {

            HttpRequest request = null;
            try {
                request = requestParser.parse(in);

                // request http version이 서버에서 지원하는지 검증
                request.validateSupportedHttpVersion(supportedHttpVersions);

                // request path에 해당하는 handler와 매칭된 path variable 찾기
                PathRouter.RoutingResult routingResult = router.route(request.getMethod(), request.getRequestTarget().getPath());

                HttpHandler handler = routingResult.handler();
                // requst에 매칭된 path variable 설정+
                request.setPathVariables(routingResult.pathVariables());

                // interceptor chain에게 요청 처리 위임
                HttpResponse response = interceptorChain.execute(request, handler);

                responseWriter.writeResponse(request.getVersion(), response, out);
                
            } catch (Exception e) {
                if (!(e instanceof HttpException))
                    logger.debug("에러 발생", e);
                // 에러 응답
                HttpResponse response = exceptionFilterChain.catchException(e, request);
                HttpVersion version = request != null ? request.getVersion() : supportedHttpVersions.get(0);
                responseWriter.writeResponse(version, response, out);
            }

        } catch (Exception e) {
            close(connection);
        }
    }


}
