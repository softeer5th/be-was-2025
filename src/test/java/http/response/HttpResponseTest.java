package http.response;

import http.enums.ContentType;
import http.enums.ErrorMessage;
import http.enums.HttpResponseStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class HttpResponseTest {

    private ByteArrayOutputStream outputStream;
    private HttpResponse httpResponse;

    @BeforeEach
    public void setUp() {
        outputStream = new ByteArrayOutputStream();
        httpResponse = new HttpResponse(outputStream);
    }

    @Test
    @DisplayName("에러 응답 전송 테스트")
    public void testSendErrorResponse() throws IOException {
        httpResponse.sendErrorResponse(HttpResponseStatus.NOT_FOUND, ErrorMessage.NOT_FOUND_PATH_AND_FILE);
        String response = outputStream.toString("UTF-8");

        assertTrue(response.startsWith("HTTP/1.1 404 Not Found"));
        assertTrue(response.contains("Content-Type: text/html;charset=utf-8"));
        assertTrue(response.contains("<h1>404 Not Found - 존재하지 않는 경로 및 파일입니다.</h1>"));
    }

    @Test
    @DisplayName("성공 응답 전송 테스트")
    public void testSendSuccessResponse() throws IOException {
        String body = "<html><body>Success</body></html>";
        httpResponse.sendSuccessResponse(HttpResponseStatus.OK, ContentType.HTML.getMimeType(), body);
        String response = outputStream.toString("UTF-8");

        assertTrue(response.startsWith("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Content-Type: text/html;charset=utf-8"));
        assertTrue(response.contains("Content-Length: " + body.getBytes("UTF-8").length));
        assertTrue(response.contains(body));
    }

    @Test
    @DisplayName("OutputStream 접근 테스트")
    public void testOutputStreamAccess() {
        assertNotNull(httpResponse.getOutputStream());
        assertSame(outputStream, httpResponse.getOutputStream());
    }
}