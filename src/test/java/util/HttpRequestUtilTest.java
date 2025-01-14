package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HttpRequestUtilTest {

    @Test
    @DisplayName("유효한 요청에서 URL 추출 테스트")
    void testGetUrlWithValidRequest() {
        String httpRequest = "GET /index.html HTTP/1.1\r\n\r\n";
        String response = HttpRequestUtil.getUrl(httpRequest);
        assertEquals("/index.html", response);
    }

    @Test
    @DisplayName("잘못된 요청에서 URL 추출 실패 테스트")
    void testGetUrlWithInvalidRequest() {
        String httpRequest = "INVALID REQUEST\r\n\r\n";
        String response = HttpRequestUtil.getUrl(httpRequest);
        assertNull(response);
    }

    @Test
    @DisplayName("빈 요청에서 URL 추출 실패 테스트")
    void testGetUrlWithEmptyRequest() {
        String httpRequest = "\r\n\r\n";
        String response = HttpRequestUtil.getUrl(httpRequest);
        assertNull(response);
    }

    @Test
    @DisplayName("헤더가 포함된 유효한 요청에서 URL 추출 테스트")
    void testGetUrlWithValidRequestWithHeaders() {
        String httpRequest = "GET /index.html HTTP/1.1\r\nHost: www.example.com\r\n\r\n";
        String response = HttpRequestUtil.getUrl(httpRequest);
        assertEquals("/index.html", response);
    }

    @Test
    @DisplayName("유효한 파일 경로에서 MIME 타입 추출 테스트")
    void testGetTypeWithValidRequest() {
        String type = HttpRequestUtil.getType("./src/main/resources/static/index.html");
        assertEquals("text/html", type);
    }

    @Test
    @DisplayName("잘못된 파일 경로에서 기본 MIME 타입 반환 테스트")
    void testGetTypeWithInvalidRequest() {
        String type = HttpRequestUtil.getType("INVALID REQUEST\r\n");
        assertEquals("application/octet-stream", type);
    }

    @Test
    @DisplayName("유효한 요청 경로 빌드 테스트")
    void testBuildPathWithValidRequest() {
        String path = HttpRequestUtil.buildPath("src/main/resources/static");
        assertEquals("src/main/resources/static/index.html", path);
    }

    @Test
    @DisplayName("리소스 파일 포함된 유효한 요청 경로 빌드 테스트")
    void testBuildPathWithValidRequestWithResourceFile() {
        String path = HttpRequestUtil.buildPath("src/main/resources/static/index.html");
        assertEquals("src/main/resources/static/index.html", path);
    }

    @Test
    @DisplayName("잘못된 요청 경로 빌드 테스트")
    void testBuildPathWithInvalidRequest() {
        String path = HttpRequestUtil.buildPath("");
        assertEquals("", path);
    }
}