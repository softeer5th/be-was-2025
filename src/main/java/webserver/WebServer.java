package webserver;

import db.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.config.ServerConfig;
import webserver.file.StaticResourceManager;
import webserver.handler.RegistrationHandler;
import webserver.handler.ServeStaticFileHandler;
import webserver.request.HttpRequestParser;
import webserver.response.HttpResponseWriter;
import webserver.router.PathRouter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServer {
    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);

    public static void main(String[] args) {

        try {
            new WebServer().start(args);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void start(String[] args) throws IOException {
        ServerConfig config = new ServerConfig();
        int port;
        if (args == null || args.length == 0) {
            port = config.getPort();
        } else {
            port = Integer.parseInt(args[0]);
        }

        ExecutorService es = Executors.newFixedThreadPool(config.getThreadPoolSize());
        Database database = new Database();
        HttpRequestParser requestParser = new HttpRequestParser(config);
        HttpResponseWriter responseWriter = new HttpResponseWriter();
        StaticResourceManager resourceManager = new StaticResourceManager(config);
        // path와 handler를 매핑한다.
        PathRouter router = new PathRouter()
                .setDefaultHandler(new ServeStaticFileHandler(resourceManager, config))
                .setHandler("/create", new RegistrationHandler(database));


        // 서버소켓을 생성한다. 웹서버는 기본적으로 8080번 포트를 사용한다.
        try (ServerSocket listenSocket = new ServerSocket(port)) {
            logger.info("Web Application Server started {} port.", port);

            // 클라이언트가 연결될때까지 대기한다.
            Socket connection;
            while ((connection = listenSocket.accept()) != null) {
                Runnable requestHandler = new FrontController(config, connection, requestParser, responseWriter, router);
                es.submit(requestHandler);
            }
        }
    }
}
