package webserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import router.RequestRouter;
import router.Router;

public class WebServer {

    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);
    private static final int DEFAULT_PORT = 8080;
    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();


    public static void main(String[] args) throws Exception {
        int port = determinePort(args);
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        Router router = new RequestRouter();

        try (ServerSocket listenSocket = new ServerSocket(port)) {
            logger.info("Web Application Server started {} port.", port);

            // 클라이언트가 연결될때까지 대기한다.
            Socket connection;
            while ((connection = listenSocket.accept()) != null) {
                executorService.submit(new RequestHandler(connection, router));
            }
            executorService.shutdown();
        }
    }

    private static int determinePort(String[] args) {
        if (args == null || args.length == 0) {
            return DEFAULT_PORT;
        }
        try {
            return Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            logger.warn("잘못된 port 입니다. 기본 포트를 사용합니다. {}", DEFAULT_PORT);
            return DEFAULT_PORT;
        }
    }

}
