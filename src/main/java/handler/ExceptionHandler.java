package handler;

import exception.ClientErrorException;
import exception.ErrorCode;
import exception.ErrorException;
import util.FileReader;

public class ExceptionHandler {
    private static final String STATIC_FILE_PATH = System.getenv("STATIC_FILE_PATH");
    private static final String ERROR_HTML = "/error.html";

    public static String handle(ErrorException e) {
        String html = FileReader.readFileAsString(STATIC_FILE_PATH + ERROR_HTML)
                .orElseThrow(() -> new ClientErrorException(ErrorCode.FILE_NOT_FOUND));
        String body = html.replace("<!--code-->", String.valueOf(e.getHttpStatus().getCode()));
        return body.replace("<!--message-->", e.getMessage());
    }
}
