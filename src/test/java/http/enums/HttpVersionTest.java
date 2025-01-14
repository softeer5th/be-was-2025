package http.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HttpVersionTest {

    @Test
    @DisplayName("유효한 HTTP 버전 검증 테스트")
    public void testValidHttpVersions() {
        assertEquals(HttpVersion.HTTP_1_0, HttpVersion.getVersionFromString("HTTP/1.0"));
        assertEquals(HttpVersion.HTTP_1_1, HttpVersion.getVersionFromString("HTTP/1.1"));
        assertEquals(HttpVersion.HTTP_2_0, HttpVersion.getVersionFromString("HTTP/2.0"));
    }

    @Test
    @DisplayName("잘못된 HTTP 버전 검증 테스트")
    public void testInvalidHttpVersions() {
        assertEquals(HttpVersion.INVALID, HttpVersion.getVersionFromString("HTTP/3.0"));
        assertEquals(HttpVersion.INVALID, HttpVersion.getVersionFromString("HTTP/1.2"));
        assertEquals(HttpVersion.INVALID, HttpVersion.getVersionFromString(""));
        assertEquals(HttpVersion.INVALID, HttpVersion.getVersionFromString(null));
    }

    @Test
    @DisplayName("HTTP 버전 문자열 반환 테스트")
    public void testGetVersion() {
        assertEquals("HTTP/1.0", HttpVersion.HTTP_1_0.getVersion());
        assertEquals("HTTP/1.1", HttpVersion.HTTP_1_1.getVersion());
        assertEquals("HTTP/2.0", HttpVersion.HTTP_2_0.getVersion());
    }

    @Test
    @DisplayName("HTTP 버전 개수 검증 테스트")
    public void testNumberOfVersions() {
        assertEquals(4, HttpVersion.values().length);
    }
}