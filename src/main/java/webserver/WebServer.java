package webserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import config.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer {
    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);

    public static void main(String[] args) throws Exception {


        int port;
        if (args == null || args.length == 0) {
            port = ServerConfig.getPort();
        } else {
            port = Integer.parseInt(args[0]);
        }

        ExecutorService executor = Executors.newFixedThreadPool(ServerConfig.getThreadPoolSize());

        // 서버소켓을 생성한다. 웹서버는 기본적으로 8080번 포트를 사용한다.
        try (ServerSocket listenSocket = new ServerSocket(port)) {
            logger.info("Web Application Server started {} port.", port);

            // 클라이언트가 연결될때까지 대기한다.
            Socket connection;
            while ((connection = listenSocket.accept()) != null) {
                executor.submit(new RequestDispatcher(connection, ServerConfig.getRouter()));
            }
            executor.shutdown();
        }
    }
}
