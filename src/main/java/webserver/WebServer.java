package webserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.resolver.ResourceResolver;
import webserver.resolver.factory.ResolverFactory;

public class WebServer {
    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);
    private static final int DEFAULT_PORT = 8080;

    private static final int MAX_THREAD_POOL_SIZE = 10;
    private static final int MIN_THREAD_POOL_SIZE = 4;
    private static final int KEEP_ALIVE_TIME = 10;
    private static final int MAX_QUEUE_SIZE = 100;


    public static void main(String args[]) throws Exception {
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                MIN_THREAD_POOL_SIZE,
                MAX_THREAD_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                workQueue
        );
        ResourceResolver resolver = ResolverFactory.createResolver();
        int port = 0;
        if (args == null || args.length == 0) {
            port = DEFAULT_PORT;
        } else {
            port = Integer.parseInt(args[0]);
        }

        // 서버소켓을 생성한다. 웹서버는 기본적으로 8080번 포트를 사용한다.
        try (ServerSocket listenSocket = new ServerSocket(port)) {
            logger.info("Web Application Server started {} port.", port);

            // 클라이언트가 연결될때까지 대기한다.
            Socket connection;
            while ((connection = listenSocket.accept()) != null) {
                executor.execute(new RequestHandler(connection, resolver));
            }
        }
    }
}
