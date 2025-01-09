package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import api.ApiRouter;
import model.RequestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.HttpRequestParser;
import webserver.http.HttpResponse;
import webserver.load.LoadResult;
import webserver.load.StaticResourceLoader;

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
            RequestData requestData = requestParser.parse(in);
            HttpResponse response = new HttpResponse(new DataOutputStream(out));

            if (handleApiRequest(requestData, response)) {
                return;
            }

            handleStaticResource(requestData, response);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private boolean handleApiRequest(RequestData requestData, HttpResponse response) throws IOException {
        LoadResult apiResult = apiRouter.route(requestData);

        if (apiResult == null) {
            return false;
        }

        if (apiResult.content() == null) {
            byte[] notFound = "<h1>400 Bad Request</h1>".getBytes();
            response.send404(notFound);
            return true;
        }

        String contentType = apiResult.contentType();
        if ("application/json".equals(contentType)) {
            String json = new String(apiResult.content(), StandardCharsets.UTF_8);
            response.sendJson(json);

        } else {
            response.send200(apiResult.content(), apiResult.path());
        }
        return true;
    }

    private void handleStaticResource(RequestData requestData, HttpResponse response) throws IOException {
        LoadResult resourceResult = resourceLoader.load(requestData.path());

        if (resourceResult.content() == null) {
            byte[] notFoundBody = "<h1>404 File Not Found</h1>".getBytes();
            response.send404(notFoundBody);
            return;
        }

        response.send200(resourceResult.content(), resourceResult.path());
    }
}