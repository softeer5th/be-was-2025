package handler;

import http.HttpRequest;
import http.HttpResponse;
import http.enums.HttpMethod;
import http.enums.HttpStatus;
import http.enums.MimeType;
import util.FileUtil;

import java.io.File;

public class FileRequestHandler implements RequestHandler{
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
        byte[] data = FileUtil.readFileToByteArray(file);

        return new HttpResponse.Builder()
                .httpStatus(HttpStatus.OK)
                .contentType(MimeType.getMimeType(extension))
                .body(data)
                .build();
    }
}
