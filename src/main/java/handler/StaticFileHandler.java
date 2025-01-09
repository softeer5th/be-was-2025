package handler;


import enums.FileContentType;
import enums.HttpStatus;
import exception.ClientErrorException;
import request.RequestInfo;
import response.HttpResponse;
import util.FileReader;

import static exception.ErrorCode.FILE_NOT_FOUND;

/*
 *  정적 파일 요청을 담당하는  Handler
 */
public class StaticFileHandler implements Handler {
    private final String STATIC_FILE_PATH;

    public StaticFileHandler() {
        STATIC_FILE_PATH = System.getenv("STATIC_FILE_PATH");
    }

    @Override
    public HttpResponse handle(RequestInfo request) {
        String path = request.getPath();

        FileContentType extension = FileContentType.getExtensionFromPath(path);

        HttpResponse response = new HttpResponse();

        response.setStatus(HttpStatus.OK);
        response.setContentType(extension);

        try {
            byte[] body = FileReader.readFile(STATIC_FILE_PATH + path)
                    .orElseThrow(() -> new ClientErrorException(FILE_NOT_FOUND));
            response.setBody(body);

        } catch (ClientErrorException e) {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setContentType(FileContentType.HTML_UTF_8);
            response.setBody(e.getMessage());

        }
        return response;
    }
}
