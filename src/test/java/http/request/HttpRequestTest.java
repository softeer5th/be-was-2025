package http.request;

import http.enums.HttpMethod;
import http.enums.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class HttpRequestTest {

    @Test
    @DisplayName("유효한 HTTP 요청 파싱 테스트")
    public void testValidHttpRequest() throws Exception {
        String rawRequest = "GET /index.html HTTP/1.1\r\n";
        InputStream in = new ByteArrayInputStream(rawRequest.getBytes());

        HttpRequest request = HttpRequestParser.parseRequest(in);

        assertEquals(HttpMethod.GET, request.getMethod());
        assertEquals("/index.html", request.getTarget().getPath());
        assertEquals(HttpVersion.HTTP_1_1, request.getVersion());
        assertFalse(request.isInvalid());
    }

    @Test
    @DisplayName("잘못된 시작 줄 처리 테스트")
    public void testInvalidStartLine() throws Exception {
        String rawRequest = "INVALID_START_LINE\r\n";
        InputStream in = new ByteArrayInputStream(rawRequest.getBytes());

        HttpRequest request = HttpRequestParser.parseRequest(in);

        assertEquals(HttpMethod.INVALID, request.getMethod());
        assertNull(request.getTarget());
        assertEquals(HttpVersion.INVALID, request.getVersion());
        assertTrue(request.isInvalid());
    }

    @Test
    @DisplayName("지원되지 않는 HTTP 메서드 처리 테스트")
    public void testUnsupportedHttpMethod() throws Exception {
        String rawRequest = "FOO /path HTTP/1.1\r\n";
        InputStream in = new ByteArrayInputStream(rawRequest.getBytes());

        HttpRequest request = HttpRequestParser.parseRequest(in);

        assertEquals(HttpMethod.INVALID, request.getMethod());
        assertEquals("/path", request.getTarget().getPath());
        assertEquals(HttpVersion.HTTP_1_1, request.getVersion());
        assertFalse(request.isInvalid());
    }

    @Test
    @DisplayName("잘못된 HTTP 버전 처리 테스트")
    public void testInvalidHttpVersion() throws Exception {
        String rawRequest = "GET /path HTTP/3.0\r\n";
        InputStream in = new ByteArrayInputStream(rawRequest.getBytes());

        HttpRequest request = HttpRequestParser.parseRequest(in);

        assertEquals(HttpMethod.GET, request.getMethod());
        assertEquals("/path", request.getTarget().getPath());
        assertEquals(HttpVersion.INVALID, request.getVersion());
        assertFalse(request.isInvalid());
    }

    @Test
    @DisplayName("빈 요청 처리 테스트")
    public void testEmptyRequest() throws Exception {
        String rawRequest = "\r\n";
        InputStream in = new ByteArrayInputStream(rawRequest.getBytes());

        HttpRequest request = HttpRequestParser.parseRequest(in);

        assertEquals(HttpMethod.INVALID, request.getMethod());
        assertNull(request.getTarget());
        assertEquals(HttpVersion.INVALID, request.getVersion());
        assertTrue(request.isInvalid());
    }
}