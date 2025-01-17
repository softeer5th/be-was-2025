package util;

import http.enums.ContentType;
import http.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HttpRequestUtilTest {

    @Test
    @DisplayName("getUrl() - 정상적인 첫 줄에서 URL 파싱")
    void testGetUrlValid() {
        String input = "GET /index.html HTTP/1.1";
        String url = HttpRequestUtil.getUrl(input);
        assertNotNull(url);
        assertEquals("/index.html", url);
    }

    @Test
    @DisplayName("getUrl() - 잘못된 첫 줄 형식으로 인해 null 반환")
    void testGetUrlInvalidFormat() {
        String input = "INVALID_LINE";
        String url = HttpRequestUtil.getUrl(input);
        assertNull(url);
    }

    @Test
    @DisplayName("getType() - 확장자를 정상적으로 파싱하여 MIME 타입 반환")
    void testGetTypeValid() {
        String input = "./src/main/resources/static/index.html";
        String mimeType = HttpRequestUtil.getType(input);
        assertEquals("text/html", mimeType);
    }

    @Test
    @DisplayName("getType() - 확장자가 없거나 인식 불가하면 DEFAULT MIME 타입 반환")
    void testGetTypeDefault() {
        String inputNoExtension = "./src/main/resources/static/index";
        String inputUnknownExt = "./src/main/resources/static/file.unknownext";
        assertEquals(ContentType.DEFAULT.getMimeType(), HttpRequestUtil.getType(inputNoExtension));
        assertEquals(ContentType.DEFAULT.getMimeType(), HttpRequestUtil.getType(inputUnknownExt));
    }

    @Test
    @DisplayName("buildPath() - 디렉토리면 기본 index.html을 붙여서 반환")
    void testBuildPathWithDirectory() {
        String path = "./src/test/resources"; // 실제 디렉토리 경로 예시
        File file = new File(path);
        if (!file.isDirectory()) {
            // 디렉토리가 없을 수도 있으니, 임시로 테스트
            // 실제 테스트 환경에서 적절한 디렉토리로 수정
            file.mkdir();
        }
        String result = HttpRequestUtil.buildPath(path);
        assertTrue(result.endsWith("index.html"), "디렉토리 경로 끝에는 index.html이 추가되어야 함");
    }

    @Test
    @DisplayName("buildPath() - 일반 파일 경로면 그대로 반환")
    void testBuildPathWithFile() {
        String path = "./src/test/resources/test.html";
        String result = HttpRequestUtil.buildPath(path);
        assertTrue(result.endsWith("test.html"), "파일인 경우 index.html을 붙이지 않아야 함");
    }

    @Test
    @DisplayName("getCookieValueByKey() - 쿠키에서 특정 key의 value를 정상적으로 파싱")
    void testGetCookieValueByKeyValid() {
        HttpRequest mockRequest = mock(HttpRequest.class);
        Map<String, String> mockHeaders = new HashMap<>();
        mockHeaders.put("Cookie", "sid=abc123; theme=dark; user=testUser");
        when(mockRequest.getHeaders()).thenReturn(mockHeaders);

        String sid = HttpRequestUtil.getCookieValueByKey(mockRequest, "sid");
        String theme = HttpRequestUtil.getCookieValueByKey(mockRequest, "theme");
        String user = HttpRequestUtil.getCookieValueByKey(mockRequest, "user");

        assertEquals("abc123", sid);
        assertEquals("dark", theme);
        assertEquals("testUser", user);
    }

    @Test
    @DisplayName("getCookieValueByKey() - 쿠키에 key가 없을 때 null 반환")
    void testGetCookieValueByKeyNotFound() {
        HttpRequest mockRequest = mock(HttpRequest.class);
        Map<String, String> mockHeaders = new HashMap<>();
        mockHeaders.put("Cookie", "sid=abc123; theme=dark");
        when(mockRequest.getHeaders()).thenReturn(mockHeaders);

        String value = HttpRequestUtil.getCookieValueByKey(mockRequest, "user");
        assertNull(value);
    }

    @Test
    @DisplayName("getCookieValueByKey() - 쿠키 헤더가 없으면 null 반환")
    void testGetCookieValueByKeyNoCookieHeader() {
        HttpRequest mockRequest = mock(HttpRequest.class);
        Map<String, String> mockHeaders = new HashMap<>(); // Cookie 헤더 자체가 없음
        when(mockRequest.getHeaders()).thenReturn(mockHeaders);

        String value = HttpRequestUtil.getCookieValueByKey(mockRequest, "sid");
        assertNull(value);
    }
}