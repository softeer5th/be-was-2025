package webserver.handler;

import util.FileUtil;
import webserver.enums.HttpStatusCode;
import webserver.file.StaticResourceManager;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

import java.io.File;
import java.util.Optional;

/**
 * 정적 파일을 응답하는 핸들러
 */
public class ServeStaticFileHandler implements HttpHandler {
    private final String defaultPageFileName;
    private final StaticResourceManager resourceManager;

    /**
     * 정적 파일을 응답하는 핸들러를 생성한다.
     *
     * @param resourceManager     정적 파일을 관리하는 매니저
     * @param defaultPageFileName 디렉토리일 경우 응답할 기본 파일 이름
     */
    public ServeStaticFileHandler(StaticResourceManager resourceManager, String defaultPageFileName) {
        this.resourceManager = resourceManager;
        this.defaultPageFileName = defaultPageFileName;
    }

    /**
     * 정적 파일을 응답한다.
     *
     * @param request HTTP 요청
     * @return 정적 파일을 담은 응답
     */
    @Override
    public HttpResponse handleGet(HttpRequest request) {
        String requestPath = request.getRequestTarget().getPath();
        // 디렉토리일 경우 디렉토리 내의 default page 파일을 요청 경로로 판단
        if (resourceManager.isDirectory(requestPath))
            requestPath = FileUtil.joinPath(requestPath, defaultPageFileName);

        Optional<File> fileOptional = resourceManager.getFile(requestPath);
        return fileOptional
                .map(file -> {
                    HttpResponse response = new HttpResponse(HttpStatusCode.OK);
                    response.setBody(file);
                    return response;
                })
                .orElseGet(() ->
                        new HttpResponse(HttpStatusCode.NOT_FOUND));
    }

}
