package util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FileUtilTest {

    private static final String VALID_FILE_PATH = "src/test/resources/test.html";
    private static final String INVALID_FILE_PATH = "src/test/resources/invalid.html";


   /* @Test
    @DisplayName("HTML 파일 byte 배열로 읽어오기 성공")
    void testReadHtmlFileAsBytes() {
        byte[] result = FileUtil.readHtmlFileAsBytes(VALID_FILE_PATH);
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("존재하지 않는 파일로 읽기 실패")
    void testReadHtmlFileAsBytesWithNonexistentFile() {
        BaseException baseException = assertThrows(BaseException.class,
            () -> FileUtil.readHtmlFileAsBytes(INVALID_FILE_PATH));
        assertEquals(baseException.getMessage(), FileErrorCode.FILE_NOT_FOUND.getMessage());
    }*/

    @Test
    @DisplayName("파일 Content Type 가져오기 성공")
    void testGetContentType() {
        String contentType = FileUtil.getContentType("test.html");
        assertEquals("text/html; charset=utf-8", contentType);
    }
}
