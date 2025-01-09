package webserver;


import enums.FileContentType;
import enums.HttpStatus;
import util.FileReader;
import util.HttpResponse;
import util.RequestInfo;

import java.io.FileNotFoundException;

/*
 *  정적 파일 요청을 담당하는  Handler
 */
public class StaticFileHandler implements Handler {
    private static final String STATIC_FILE_PATH = "src/main/resources/static";

    @Override
    public HttpResponse handle(RequestInfo request) {
        String path = request.getPath();

        FileContentType extension = FileContentType.getExtensionFromPath(path);

        HttpResponse response = new HttpResponse();

        response.setStatus(HttpStatus.OK);
        response.setContentType(extension);

        try {
            byte[] body = FileReader.readFile(STATIC_FILE_PATH + path)
                    .orElseThrow(() -> new FileNotFoundException(path));
            response.setBody(body);

        } catch (FileNotFoundException e) {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setContentType(FileContentType.HTML);
            response.setBody("file not found");

        }
        return response;
    }
}
