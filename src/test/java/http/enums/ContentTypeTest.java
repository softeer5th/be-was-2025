package http.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContentTypeTest {

    @Test
    @DisplayName("ContentType의 확장자 반환 테스트")
    void testGetExtension() {
        ContentType contentType = ContentType.CSS;
        String result = contentType.getExtension();
        assertEquals("css", result);
    }

    @Test
    @DisplayName("ContentType의 MIME 타입 반환 테스트")
    void testGetMimeType() {
        ContentType contentType = ContentType.HTML;
        String result = contentType.getMimeType();
        assertEquals("text/html", result);
    }

    @Test
    @DisplayName("유효한 확장자로 MIME 타입 반환 테스트")
    void testGetMimeTypeExtensionWithValidExtension() {
        String result = ContentType.getMimeTypeByExtension("jpg");
        assertEquals("image/jpeg", result);
    }

    @Test
    @DisplayName("잘못된 확장자로 기본 MIME 타입 반환 테스트")
    void testGetMimeTypeExtensionWithInvalidExtension() {
        String result = ContentType.getMimeTypeByExtension("jpdfdg");
        assertEquals("application/octet-stream", result);
    }
}