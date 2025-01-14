package http.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpMethodTest {

    @Test
    @DisplayName("유효한 HTTP 메서드 검증 테스트")
    public void testValidHttpMethods() {
        assertEquals(HttpMethod.GET, HttpMethod.getMethodFromString("GET"));
        assertEquals(HttpMethod.POST, HttpMethod.getMethodFromString("POST"));
        assertEquals(HttpMethod.PUT, HttpMethod.getMethodFromString("PUT"));
        assertEquals(HttpMethod.DELETE, HttpMethod.getMethodFromString("DELETE"));
        assertEquals(HttpMethod.PATCH, HttpMethod.getMethodFromString("PATCH"));
        assertEquals(HttpMethod.HEAD, HttpMethod.getMethodFromString("HEAD"));
        assertEquals(HttpMethod.OPTIONS, HttpMethod.getMethodFromString("OPTIONS"));
    }

    @Test
    @DisplayName("잘못된 HTTP 메서드 검증 테스트")
    public void testInvalidHttpMethod() {
        assertEquals(HttpMethod.INVALID, HttpMethod.getMethodFromString("FOO"));
        assertEquals(HttpMethod.INVALID, HttpMethod.getMethodFromString(""));
        assertEquals(HttpMethod.INVALID, HttpMethod.getMethodFromString(null));
    }

    @Test
    @DisplayName("HTTP 메서드 문자열 반환 테스트")
    public void testGetMethod() {
        assertEquals("GET", HttpMethod.GET.getMethod());
        assertEquals("POST", HttpMethod.POST.getMethod());
        assertEquals("PUT", HttpMethod.PUT.getMethod());
    }
}