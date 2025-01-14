package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import api.ApiRouter;
import global.model.HttpRequest;
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

        // todo: apiResult에 content가 없을 때 404 처리

        if ("redirect".equals(apiResult.contentType())) {
            logger.debug("리다이렉션 응답입니다.");
            response.sendRedirect(apiResult.path());
            return true;
        }

        String contentType = apiResult.contentType();
        if ("application/json".equals(contentType)) {
            logger.debug("JSON 응답입니다.");
            String json = new String(apiResult.content(), StandardCharsets.UTF_8);
            response.sendJson(json);

        } else {
            response.send200(apiResult.content(), apiResult.path());
        }
        return true;
    }

    private void handleStaticResource(HttpRequest httpRequest, HttpResponse response) throws IOException {
        LoadResult resourceResult = resourceLoader.load(httpRequest.path());

        if (resourceResult.content() == null) {
            byte[] notFoundBody = "<h1>404 File Not Found</h1>".getBytes();
            response.send404(notFoundBody);
            return;
        }

        response.send200(resourceResult.content(), resourceResult.path());
    }
}