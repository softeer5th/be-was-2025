package util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestUtilTest {
    @Test
    void testGetUrlWithValidRequest() {
        String httpRequest = "GET /index.html HTTP/1.1\r\n\r\n";
        String response = HttpRequestUtil.getUrl(httpRequest);
        assertEquals("/index.html", response);
    }

    @Test
    void testGetUrlWithInvalidRequest() {
        String httpRequest = "INVALID REQUEST\r\n\r\n";
        String response = HttpRequestUtil.getUrl(httpRequest);
        assertNull(response);
    }

    @Test
    void testGetUrlWithEmptyRequest() {
        String httpRequest = "\r\n\r\n";
        String response = HttpRequestUtil.getUrl(httpRequest);
        assertNull(response);
    }

    @Test
    void testGetUrlWithValidRequestWithHeaders() {
        String httpRequest = "GET /index.html HTTP/1.1\r\nHost: www.example.com\r\n\r\n";
        String response = HttpRequestUtil.getUrl(httpRequest);
        assertEquals("/index.html", response);
    }

    @Test
    void testGetTypeWithValidRequest() {
        String type = HttpRequestUtil.getType("./src/main/resources/static/index.html");
        assertEquals("text/html", type);
    }

    @Test
    void testGetTypeWithInvalidRequest() {
        String type = HttpRequestUtil.getType("INVALID REQUEST\r\n");
        assertEquals("application/octet-stream", type);
    }

    @Test
    void testBuildPathWithValidRequest() {
        String path = HttpRequestUtil.buildPath("src/main/resources/static");
        assertEquals("src/main/resources/static/index.html", path);
    }

    @Test
    void testBuildPathWithValidRequestWithResourceFile() {
        String path = HttpRequestUtil.buildPath("src/main/resources/static/index.html");
        assertEquals("src/main/resources/static/index.html", path);
    }

    @Test
    void testBuildPathWithInvalidRequest() {
        String path = HttpRequestUtil.buildPath("");
        assertEquals("", path);
    }
}
