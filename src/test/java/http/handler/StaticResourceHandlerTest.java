package http.handler;

import http.request.HttpRequest;
import http.request.TargetInfo;
import http.response.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import util.FileUtil;
import util.HttpRequestUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StaticResourceHandlerTest {

    private StaticResourceHandler handler;
    private HttpRequest mockRequest;
    private HttpResponse mockResponse;
    private OutputStream out;
    private static MockedStatic<HttpRequestUtil> mockHttpRequestUtil;
    private static MockedStatic<FileUtil> mockFileUtil;

    @BeforeEach
    public void setUp() {
        handler = StaticResourceHandler.getInstance("./src/test/resources/static/");
        mockRequest = mock(HttpRequest.class);
        out = new ByteArrayOutputStream();
        mockHttpRequestUtil = mockStatic(HttpRequestUtil.class);
        mockFileUtil = mockStatic(FileUtil.class);
    }

    @AfterEach
    public void tearDown() {
        mockHttpRequestUtil.close();
        mockFileUtil.close();
    }

    @Test
    @DisplayName("정적 리소스를 성공적으로 처리")
    public void testHandleValidStaticResource() throws IOException, URISyntaxException {
        String testPath = "/index.html";
        String testContent = "<html><body>Test</body></html>";
        String contentType = "text/html";

        TargetInfo targetInfo = new TargetInfo(testPath);
        when(mockRequest.getTarget()).thenReturn(targetInfo);

        mockHttpRequestUtil.when(() -> HttpRequestUtil.buildPath(anyString()))
                .thenReturn("./src/test/resources/static" + testPath);
        mockHttpRequestUtil.when(() -> HttpRequestUtil.getType(anyString()))
                .thenReturn(contentType);
        mockFileUtil.when(() -> FileUtil.fileToByteArray(anyString()))
                .thenReturn(testContent.getBytes());

        mockResponse = handler.handle(mockRequest);

        mockResponse.send(out);
        String responseStatusLine = out.toString().split("\r\n")[0];
        assertEquals("HTTP/1.1 200 OK", responseStatusLine);
    }

    @Test
    @DisplayName("존재하지 않는 리소스 요청")
    public void testHandleNonExistentResource() throws IOException, URISyntaxException {
        String testPath = "/nonexistent.html";

        TargetInfo targetInfo = new TargetInfo(testPath);
        when(mockRequest.getTarget()).thenReturn(targetInfo);

        mockHttpRequestUtil.when(() -> HttpRequestUtil.buildPath(anyString()))
                .thenReturn("./src/test/resources/static" + testPath);
        mockFileUtil.when(() -> FileUtil.fileToByteArray(anyString()))
                .thenReturn(null);

        mockResponse = handler.handle(mockRequest);

        mockResponse.send(out);
        String responseStatusLine = out.toString().split("\r\n")[0];
        assertEquals("HTTP/1.1 404 Not Found", responseStatusLine);
    }

    @Test
    @DisplayName("파일 읽기 중 예외 처리")
    public void testHandleFileReadException() throws IOException, URISyntaxException {
        String testPath = "/index.html";

        TargetInfo targetInfo = new TargetInfo(testPath);
        when(mockRequest.getTarget()).thenReturn(targetInfo);

        mockHttpRequestUtil.when(() -> HttpRequestUtil.buildPath(anyString()))
                .thenReturn("./src/test/resources/static" + testPath);
        mockFileUtil.when(() -> FileUtil.fileToByteArray(anyString()))
                .thenThrow(new IOException("Test exception"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> handler.handle(mockRequest));
        assertInstanceOf(IOException.class, exception.getCause());
    }
}