package http.handler;

import http.enums.HttpResponseStatus;
import http.request.HttpRequest;
import http.request.TargetInfo;
import http.response.HttpResponse;
import util.FileUtil;
import util.HttpRequestUtil;

import java.io.IOException;

public class StaticResourceHandler implements Handler {
    private static final String STATIC_RESOURCE_PATH = "./src/main/resources/static";
    private static final String NOT_FOUND_MESSAGE = "<h1>404 Not Found</h1>";

    private static StaticResourceHandler instance = new StaticResourceHandler();

    private StaticResourceHandler() {}

    public static StaticResourceHandler getInstance() {
        return instance;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        TargetInfo target = request.getTarget();
        String path = STATIC_RESOURCE_PATH + target.getPath();

        path = HttpRequestUtil.buildPath(path);
        String type = HttpRequestUtil.getType(path); // 파일 유형 별로 Content-Type 할당

        String body; // 해당 파일의 경로를 byte로 전달
        try {
            byte[] file = FileUtil.fileToByteArray(path);
            if (file != null) {
                body = new String(file);
                response.sendSuccessResponse(HttpResponseStatus.OK, type, body);
            } else {
                response.sendErrorResponse(HttpResponseStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
