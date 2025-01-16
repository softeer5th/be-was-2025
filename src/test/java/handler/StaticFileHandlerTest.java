package handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import exception.BaseException;
import exception.FileErrorCode;
import http.HttpMethod;
import http.HttpRequestInfo;
import http.HttpResponse;
import http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StaticFileHandlerTest {

    private static final String VALID_FILE_PATH = "/index.html";
    private static final String INVALID_FILE_PATH = "/invalid.html";

    @Test
    @DisplayName("정적파일 로드 성공")
    void testHandleWithExistingFile() {
        StaticFileHandler handler = new StaticFileHandler();
        HttpRequestInfo request = new HttpRequestInfo(HttpMethod.GET, VALID_FILE_PATH, null);

        HttpResponse response = handler.handle(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("정적파일 로드 실패")
    void testHandleWithFileNotFound() {
        StaticFileHandler handler = new StaticFileHandler();
        HttpRequestInfo request = new HttpRequestInfo(HttpMethod.GET, INVALID_FILE_PATH, null);

        BaseException baseException = assertThrows(BaseException.class,
            () -> handler.handle(request));
        assertEquals(baseException.getMessage(), FileErrorCode.FILE_NOT_FOUND.getMessage());
    }

}
