package http.handler;

import http.enums.ErrorMessage;
import http.enums.HttpResponseStatus;
import http.request.HttpRequest;
import http.request.TargetInfo;
import http.response.HttpResponse;
import util.FileUtil;
import util.HttpRequestUtil;

import java.io.IOException;

public class StaticResourceHandler implements Handler {
    private final String staticResourcePath;

    private static volatile StaticResourceHandler instance;

    private StaticResourceHandler(String staticResourcePath) {
        this.staticResourcePath = staticResourcePath;
    }

    public static StaticResourceHandler getInstance(String staticResourcePath) {
        if (instance == null) {
            synchronized (StaticResourceHandler.class) {
                if (instance == null) {
                    instance = new StaticResourceHandler(staticResourcePath);
                }
            }
        }
        return instance;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        TargetInfo target = request.getTarget();
        String path = staticResourcePath + target.getPath();

        path = HttpRequestUtil.buildPath(path);
        String type = HttpRequestUtil.getType(path); // 파일 유형 별로 Content-Type 할당

        String body; // 해당 파일의 경로를 byte로 전달
        try {
            byte[] file = FileUtil.fileToByteArray(path);
            if (file != null) {
                body = new String(file);
                response.sendSuccessResponse(HttpResponseStatus.OK, type, body);
            } else {
                response.sendErrorResponse(HttpResponseStatus.NOT_FOUND, ErrorMessage.NOT_FOUND_PATH_AND_FILE);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
