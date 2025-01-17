package http.request;

import http.enums.HttpMethod;
import http.enums.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HttpRequestParserTest {

    @Test
    @DisplayName("정상적인 HTTP 요청 파싱 테스트")
    public void testParseValidHttpRequest() throws IOException, URISyntaxException {
        String rawRequest =
                "POST /submit HTTP/1.1\r\n" +
                        "Host: localhost\r\n" +
                        "Content-Type: application/x-www-form-urlencoded\r\n" +
                        "Content-Length: 27\r\n" +
                        "\r\n" +
                        "name=John&age=30&city=Seoul";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes());

        HttpRequest httpRequest = HttpRequestParser.parseRequest(inputStream);

        assertEquals(HttpMethod.POST, httpRequest.getMethod());
        assertEquals("/submit", httpRequest.getTarget().getPath());
        assertEquals(HttpVersion.HTTP_1_1, httpRequest.getVersion());
        assertNotNull(httpRequest.getHeaders());
        assertNotNull(httpRequest.getBody());
        assertEquals("name=John&age=30&city=Seoul", httpRequest.getBody());
    }

    @Test
    @DisplayName("HTTP Body를 Map으로 파싱 테스트")
    public void testParseRequestBodyToMap() throws IOException {
        String requestBody = "name=John&age=30&city=Seoul";

        Map<String, Object> parsedBody = HttpRequestParser.parseRequestBody(requestBody);

        assertEquals(3, parsedBody.size());
        assertEquals("John", parsedBody.get("name"));
        assertEquals("30", parsedBody.get("age"));
        assertEquals("Seoul", parsedBody.get("city"));
    }

    @Test
    @DisplayName("Content-Length가 없는 요청 처리 테스트")
    public void testParseHttpRequestWithoutContentLength() throws IOException, URISyntaxException {
        String rawRequest =
                "GET /hello HTTP/1.1\r\n" +
                        "Host: localhost\r\n" +
                        "\r\n";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes());

        HttpRequest httpRequest = HttpRequestParser.parseRequest(inputStream);

        assertEquals(HttpMethod.GET, httpRequest.getMethod());
        assertEquals("/hello", httpRequest.getTarget().getPath());
        assertEquals(HttpVersion.HTTP_1_1, httpRequest.getVersion());
        assertNotNull(httpRequest.getHeaders());
        assertNull(httpRequest.getBody());
    }

    @Test
    @DisplayName("잘못된 HTTP 요청 파싱 테스트")
    public void testParseInvalidHttpRequest() throws IOException, URISyntaxException {
        String rawRequest = "INVALID REQUEST\r\n";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes());

        HttpRequest httpRequest = HttpRequestParser.parseRequest(inputStream);

        assertEquals(HttpMethod.INVALID, httpRequest.getMethod());
        assertNull(httpRequest.getTarget());
        assertEquals(HttpVersion.INVALID, httpRequest.getVersion());
        assertNull(httpRequest.getHeaders());
        assertNull(httpRequest.getBody());
    }
}