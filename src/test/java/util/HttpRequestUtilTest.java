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
    void testGetUrlWithInValidRequest() {
        String httpRequest = "INVALID REQUEST\r\n\r\n";
        String response = HttpRequestUtil.getUrl(httpRequest);
        assertNull(response);
    }
}
