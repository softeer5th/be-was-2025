package handler;

import http.HttpRequestInfo;
import http.HttpStatus;
import exception.BaseException;
import exception.FileErrorCode;
import util.FileUtil;
import http.HttpResponse;

public class StaticFileHandler implements Handler {

    private final String STATIC_FILE_PATH = "src/main/resources/static";

    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        String path = request.getPath();

        // 디렉토리 요청이면 "/index.html" 추가
        if (!path.contains(".")) {
            path += "/index.html";
        }
        String fileExtension = FileUtil.getContentType(path);

        HttpResponse response = new HttpResponse();

        byte[] body = FileUtil.readHtmlFileAsBytes(STATIC_FILE_PATH + path);
        if (body == null) {
            throw new BaseException(FileErrorCode.FILE_NOT_FOUND);
        }
        response.setStatus(HttpStatus.OK);
        response.setContentType(fileExtension);
        response.setBody(body);

        return response;
    }
}
