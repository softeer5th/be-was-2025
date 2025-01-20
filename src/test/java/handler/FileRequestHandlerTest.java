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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FileRequestHandlerTest {

    private static final HttpMethod HTTP_METHOD = HttpMethod.GET;
    private static final String VALID_FILE_PATH = "/index.html";
    private static final String INVALID_FILE_PATH = "/invalid.html";


    private HttpRequestInfo createHttpRequest(HttpMethod method, String path) throws IOException {
        String rawRequest =
                method + " " + path + " HTTP/1.1\r\n" +
                        "Host: localhost";

        InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));
        return new HttpRequestInfo(inputStream);
    }

    @Test
    @DisplayName("정적파일 로드 성공")
    void testHandleWithExistingFile() throws IOException {
        FileRequestHandler handler = new FileRequestHandler();
        HttpRequestInfo request = createHttpRequest(HTTP_METHOD, VALID_FILE_PATH);

        HttpResponse response = handler.handle(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("정적파일 로드 실패")
    void testHandleWithFileNotFound() throws IOException {
        FileRequestHandler handler = new FileRequestHandler();
        HttpRequestInfo request = createHttpRequest(HTTP_METHOD, INVALID_FILE_PATH);

        BaseException baseException = assertThrows(BaseException.class,
                () -> handler.handle(request));
        assertEquals(baseException.getMessage(), FileErrorCode.FILE_NOT_FOUND.getMessage());
    }

}
