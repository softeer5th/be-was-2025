package webserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer {
    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);
    private static final int DEFAULT_PORT = 8080;
    private static final int NUM_INITIALIZED_THREADS = 20;
    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(NUM_INITIALIZED_THREADS);

    public static void main(String args[]) throws Exception {
        int port = 0;
        if (args == null || args.length == 0) {
            port = DEFAULT_PORT;
        } else {
            port = Integer.parseInt(args[0]);
        }

        try (ServerSocket listenSocket = new ServerSocket(port)) {
            logger.info("Web Application Server started {} port.", port);
            Socket connection;
            while ((connection = listenSocket.accept()) != null) {
                THREAD_POOL.execute(new RequestHandler(connection));
            }
        } finally {
            THREAD_POOL.shutdown();
            logger.info("Web Application Server stopped.");
        }
    }
}
