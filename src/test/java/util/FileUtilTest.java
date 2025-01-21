package util;

import exception.BaseException;
import exception.FileErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FileUtilTest {

    private static final String VALID_FILE_PATH = "src/test/resources/test.html";
    private static final String INVALID_FILE_PATH = "src/test/resources/invalid.html";


   @Test
    @DisplayName("HTML 파일 byte 배열로 읽어오기 성공")
    void testReadHtmlFileAsBytes() {
        String result = FileUtil.readHtmlFileAsString(VALID_FILE_PATH);
        assertNotNull(result);
       assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("존재하지 않는 파일로 읽기 실패")
    void testReadHtmlFileAsBytesWithNonexistentFile() {
        BaseException baseException = assertThrows(BaseException.class,
            () -> FileUtil.readHtmlFileAsString(INVALID_FILE_PATH));
        assertEquals(baseException.getMessage(), FileErrorCode.FILE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("파일 Content Type 가져오기 성공")
    void testGetContentType() {
        String contentType = FileUtil.getContentType("test.html");
        assertEquals("text/html; charset=utf-8", contentType);
    }
}
