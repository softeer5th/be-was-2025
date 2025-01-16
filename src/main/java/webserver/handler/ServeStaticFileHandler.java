package webserver.handler;

import util.FileUtil;
import webserver.config.ServerConfig;
import webserver.enums.HttpStatusCode;
import webserver.file.StaticResourceManager;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;
import webserver.view.ModelAndTemplate;

import java.io.File;
import java.util.Optional;

// staic 파일을 응답
public class ServeStaticFileHandler implements HttpHandler {
    private final String defaultPageFileName;
    private final StaticResourceManager resourceManager;
    private final String templateFileExtension;

    public ServeStaticFileHandler(StaticResourceManager resourceManager, ServerConfig config) {
        this.resourceManager = resourceManager;
        this.defaultPageFileName = config.getDefaultPageFileName();
        templateFileExtension = config.getTemplateFileExtension();
    }

    @Override
    public HttpResponse handleGet(HttpRequest request) {
        String requestPath = request.getRequestTarget().getPath();
        // 디렉토리일 경우 디렉토리 내의 default page 파일을 요청 경로로 판단
        if (resourceManager.isDirectory(requestPath))
            requestPath = FileUtil.joinPath(requestPath, defaultPageFileName);

        if (isTemplateFile(requestPath)) {
            ModelAndTemplate modelAndTemplate = new ModelAndTemplate(requestPath);
            HttpResponse response = new HttpResponse(HttpStatusCode.OK);
            response.renderTemplate(modelAndTemplate);
            return response;
        }

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

    private boolean isTemplateFile(String requestPath) {
        return requestPath.endsWith(templateFileExtension);
    }
}
