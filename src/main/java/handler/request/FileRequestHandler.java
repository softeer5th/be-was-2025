package handler.request;

import handler.mapping.DynamicHtmlHandlerMapping;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.enums.HttpMethod;
import http.enums.HttpStatus;
import http.enums.MimeType;
import util.FileUtil;

import java.io.File;

public class FileRequestHandler implements RequestHandler{
    private final DynamicHtmlHandlerMapping dynamicHtmlHandlerMapping = DynamicHtmlHandlerMapping.getInstance();


    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        if(httpRequest.getMethod() == HttpMethod.GET && FileUtil.isFileExist(httpRequest.getPath())){
            return true;
        }
        return false;
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        File file = FileUtil.getFile(httpRequest.getPath());

        String extension = FileUtil.extractFileExtension(file.getPath());
        byte[] fileData = FileUtil.readFileToByteArray(file);

        return dynamicHtmlHandlerMapping.getHandler(httpRequest.getPath())
                .map(handler -> handler.handle(fileData, extension, httpRequest))
                .orElse(new HttpResponse.Builder()
                        .httpStatus(HttpStatus.OK)
                        .contentType(MimeType.getMimeType(extension))
                        .body(fileData)
                        .build());
    }
}
