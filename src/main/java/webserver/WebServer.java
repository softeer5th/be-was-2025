package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.config.ServerConfig;
import webserver.request.HttpRequestParser;
import webserver.response.HttpResponseWriter;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServer {
    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);

    public static void main(String args[]) throws Exception {
        ServerConfig config = new ServerConfig();
        int port;
        if (args == null || args.length == 0) {
            port = config.getPort();
        } else {
            port = Integer.parseInt(args[0]);
        }

        ExecutorService es = Executors.newCachedThreadPool();
        HttpRequestParser requestParser = new HttpRequestParser();
        HttpResponseWriter responseWriter = new HttpResponseWriter();

        // 서버소켓을 생성한다. 웹서버는 기본적으로 8080번 포트를 사용한다.
        try (ServerSocket listenSocket = new ServerSocket(port)) {
            logger.info("Web Application Server started {} port.", port);

            // 클라이언트가 연결될때까지 대기한다.
            Socket connection;
            while ((connection = listenSocket.accept()) != null) {
                Runnable requestHandler = new RequestHandler(connection, requestParser, responseWriter, config);
                es.submit(requestHandler);
            }
        }
    }
}
