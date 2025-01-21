package webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import controller.HomeController;
import controller.SignUpController;
import controller.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlet.*;
import webserver.httpserver.HttpRequestFactory;
import webserver.httpserver.RequestHandler;

public class WebServer {
    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);
    private static final int DEFAULT_PORT = 8080;

    public static void main(String args[]) throws Exception {
        int port = 0;
        if (args == null || args.length == 0) {
            port = DEFAULT_PORT;
        } else {
            port = Integer.parseInt(args[0]);
        }

        ExecutorService executor = Executors.newFixedThreadPool(200);
        ServletManager servletManager = new ServletManager(List.of(
                new HomeController(),
                new SignUpController(),
                new UserController()),
                new HttpRequestFactory()
        );

        runServer(port, executor, servletManager);
    }

    private static void runServer(int port, ExecutorService executor, ServletManager servletManager) {
        // 서버소켓을 생성한다. 웹서버는 기본적으로 8080번 포트를 사용한다.
        try (ServerSocket listenSocket = new ServerSocket(port)) {
            logger.info("Web Application Server started {} port.", port);

            // 클라이언트가 연결될때까지 대기한다.
            Socket connection;
            while ((connection = listenSocket.accept()) != null) {
                executor.submit(new RequestHandler(connection, servletManager));
            }
        } catch (IOException e){
            logger.error(e.getMessage(), e);
        }
    }
}
