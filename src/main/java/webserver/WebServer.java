package webserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer {
    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);
    private static final int DEFAULT_PORT = 8080;
    private static final int THREAD_POOL_SIZE = 10;


    public static void main(String args[]) throws Exception {
        int port = 0;
        if (args == null || args.length == 0) {
            port = DEFAULT_PORT;
        } else {
            port = Integer.parseInt(args[0]);
        }

        // 서버소켓을 생성한다. 웹서버는 기본적으로 8080번 포트를 사용한다.
        try (ServerSocket listenSocket = new ServerSocket(port)) {
            logger.info("Web Application Server started on port {}.", port);

            // 스레드 풀을 생성하여 작업을 처리한다.
            ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

            while (true) {
                Socket connection = listenSocket.accept();
                // 새로운 클라이언트 연결에 대해 요청을 처리할 작업을 스레드 풀에 제출
                executorService.submit(new RequestHandler(connection));
            }
        } catch (Exception e) {
            logger.error("Error occurred while running the server: ", e);
        }
    }
}
