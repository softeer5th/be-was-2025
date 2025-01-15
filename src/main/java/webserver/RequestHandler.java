package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import api.ApiRouter;
import global.model.HttpRequest;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import global.model.HttpResponse;
import global.model.LoadResult;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final StaticResourceLoader resourceLoader = new StaticResourceLoader("src/main/resources/static");
    private final HttpRequestParser requestParser = new HttpRequestParser();
    private final ApiRouter apiRouter = new ApiRouter();
    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    @Override
    public void run() {
        logger.debug("New Client Connect! IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest httpRequest = requestParser.parse(in);
            HttpResponse response = new HttpResponse(new DataOutputStream(out));

            if (handleApiRequest(httpRequest, response)) {
                return;
            }

            handleStaticResource(httpRequest, response);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private boolean handleApiRequest(HttpRequest httpRequest, HttpResponse response) throws IOException {
        LoadResult apiResult = apiRouter.route(httpRequest);

        if (apiResult == null) {
            return false;
        }

        String contentType = apiResult.contentType();

        // todo: apiResult에 content가 없을 때 404 처리

        if ("redirect".equals(contentType)) {
            logger.debug("리다이렉션 응답입니다.");
            response.sendRedirect(apiResult.path());
            return true;
        }

        if ("application/json".equals(contentType)) {
            logger.debug("JSON 응답입니다.");
            String json = new String(apiResult.content(), StandardCharsets.UTF_8);

            if (apiResult.cookie() != null && !apiResult.cookie().isBlank()) {
                logger.debug("쿠키 값: {}", apiResult.cookie());
                response.sendJsonWithCookie(json, apiResult.cookie());
            } else {
                response.sendJson(json);
            }
            return true;
        }

        response.send200(apiResult.content(), apiResult.path());
        return true;
    }

    private void handleStaticResource(HttpRequest httpRequest, HttpResponse response) throws IOException {
        String path = httpRequest.path();
        if ("/index.html".equals(path)) {
            String sid = extractSidFromCookie(httpRequest);
            if (sid != null) {
                User user = SessionManager.getUser(sid);
                if (user != null) {
                    path = "/main/index.html";
                }
            }
        }

        LoadResult resourceResult = resourceLoader.load(path);

        if (resourceResult.content() == null) {
            byte[] notFoundBody = "<h1>404 File Not Found</h1>".getBytes();
            response.send404(notFoundBody);
            return;
        }

        response.send200(resourceResult.content(), resourceResult.path());
    }

    private String extractSidFromCookie(HttpRequest request) {
        Map<String, String> headers = request.headers();
        if (headers == null) {
            return null;
        }

        String cookieHeader = headers.get("Cookie");
        if (cookieHeader == null) {
            return null;
        }

        String[] cookiePairs = cookieHeader.split(";");
        for (String pair : cookiePairs) {
            String[] kv = pair.trim().split("=", 2);
            if (kv.length == 2 && "SID".equals(kv[0])) {
                return kv[1];
            }
        }
        return null;
    }
}