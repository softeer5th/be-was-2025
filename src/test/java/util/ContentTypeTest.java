package util;

import http.response.ContentType;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContentTypeTest {
    @Mock
    ContentType contentType;

    @Test
    void testGetExtension() {
        contentType = ContentType.CSS;
        String result = contentType.getExtension();
        assertEquals("css", result);
    }

    @Test
    void testGetMimeType() {
        contentType = ContentType.HTML;
        String result = contentType.getMimeType();
        assertEquals("text/html", result);
    }

    @Test
    void testGetMimeTypeExtensionWithValidExtension() {
        String result = ContentType.getMimeTypeByExtension("jpg");
        assertEquals("image/jpeg", result);
    }

    @Test
    void testGetMimeTypeExtensionWithInvalidExtension() {
        String result = ContentType.getMimeTypeByExtension("jpdfdg");
        assertEquals("application/octet-stream", result);
    }
}
