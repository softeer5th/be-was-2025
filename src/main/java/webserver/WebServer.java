package webserver;

import db.Database;
import db.DatabaseInitializer;
import domain.ArticleDao;
import domain.CommentDao;
import domain.UserDao;
import handler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.config.ServerConfig;
import webserver.exception.filter.ExceptionFilterChain;
import webserver.exception.filter.HttpExceptionFilter;
import webserver.exception.filter.LoginRequiredFilter;
import webserver.file.StaticResourceManager;
import webserver.handler.ServeStaticFileHandler;
import webserver.interceptor.HandlerInterceptor;
import webserver.interceptor.InterceptorChain;
import webserver.interceptor.LoggingInterceptor;
import webserver.interceptor.LoginRequiredPathInterceptor;
import webserver.request.HttpRequestParser;
import webserver.response.HttpResponseWriter;
import webserver.router.PathRouter;
import webserver.session.MemorySessionManager;
import webserver.session.SessionInterceptor;
import webserver.session.SessionManager;
import webserver.view.*;
import webserver.view.renderer.ForeachTagRenderer;
import webserver.view.renderer.IfTagRenderer;
import webserver.view.renderer.IncludeTagRenderer;
import webserver.view.renderer.TextTagRenderer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static enums.PageMappingPath.*;

/**
 * <pre>
 * 웹 서버 클래스
 * 1. 클라이언트의 요청을 받아들이고, 응답을 보내는 역할을 한다.
 * 2. 각 객체들을 생성하고 의존성을 연결해주는 역할을 한다.
 * </pre>
 */
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

        Database database = new Database(config.getJdbcUrl(), config.getUsername(), config.getPassword());
        DatabaseInitializer initializer = new DatabaseInitializer(database);
        // 데이터베이스 초기화
        initializer.initTables();
        UserDao userDao = new UserDao(database);
        ArticleDao articleDao = new ArticleDao(database);
        CommentDao commentDao = new CommentDao(database);

        HttpRequestParser requestParser = new HttpRequestParser(config.getMaxHeaderSize());
        HttpResponseWriter responseWriter = new HttpResponseWriter();

        StaticResourceManager resourceManager = new StaticResourceManager(config.getStaticDirectory(), config.getUploadDirectory());
        TemplateFileReader templateFileReader = new TemplateFileReaderImpl(config.getTemplateDirectory());
        TemplateEngine templateEngine = new MyTemplateEngine()
                .registerTagHandler(new ForeachTagRenderer())
                .registerTagHandler(new IfTagRenderer())
                .registerTagHandler(new TextTagRenderer())
                .registerTagHandler(new IncludeTagRenderer(templateFileReader));

        // path와 handler를 매핑한다.
        PathRouter router = new PathRouter()
                .setDefaultHandler(new ServeStaticFileHandler(resourceManager, config.getDefaultPageFileName()))
                .setHandler(INDEX.path, new IndexPageHandler(articleDao, commentDao))
                .setHandler(READ_ARTICLE.path, new ReadArticleHandler(articleDao, commentDao))
                .setHandler(REGISTRATION.path, new RegistrationHandler(userDao))
                .setHandler(LOGIN.path, new LoginHandler(userDao))
                .setHandler(LOGOUT.path, new LogoutHandler())
                .setHandler(MYPAGE.path, new MypageHandler(userDao, resourceManager))
                .setHandler(WRITE_ARTICLE.path, new WriteArticleHandler(articleDao, resourceManager))
                .setHandler(WRITE_COMMENT.path, new WriteCommentHandler(database, articleDao, commentDao));

        SessionManager sessionManager = new MemorySessionManager();

        HandlerInterceptor sessionInterceptor = new SessionInterceptor(sessionManager);
        HandlerInterceptor logInterceptor = new LoggingInterceptor();
        HandlerInterceptor templateInterceptor = new TemplateEngineInterceptor(templateEngine, templateFileReader);
        HandlerInterceptor loginRequiredInterceptor = new LoginRequiredPathInterceptor(
                MYPAGE.path, WRITE_ARTICLE.path, WRITE_COMMENT.path);
        InterceptorChain interceptorChain = InterceptorChain
                .inbound()
                .add(sessionInterceptor)
                .add(loginRequiredInterceptor)
                .add(logInterceptor)
                .outbound()
                .add(sessionInterceptor)
                .add(templateInterceptor)
                .add(logInterceptor)
                .build();

        ExceptionFilterChain exceptionFilterChain = new ExceptionFilterChain()
                .addFilter(new LoginRequiredFilter())
                .addFilter(new HttpExceptionFilter());


        // 서버소켓을 생성한다. 웹서버는 기본적으로 8080번 포트를 사용한다.
        try (ServerSocket listenSocket = new ServerSocket(port)) {
            logger.info("Web Application Server started {} port.", port);

            // 클라이언트가 연결될때까지 대기한다.
            Socket connection;
            while ((connection = listenSocket.accept()) != null) {
                Runnable requestHandler = new FrontController(config, connection, requestParser, responseWriter, router, interceptorChain, exceptionFilterChain);
                es.submit(requestHandler);
            }
        }
    }
}
