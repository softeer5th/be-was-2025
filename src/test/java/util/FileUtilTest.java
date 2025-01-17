package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FileUtilTest {

    @Test
    @DisplayName("유효한 파일 경로에 대한 바이트 배열 반환 테스트")
    void testFileToByteArrayWithValidRequest() throws IOException {
        String path = "./src/main/resources/static/index.html";
        byte[] fileBytes = FileUtil.fileToByteArray(path);
        byte[] expectedBytes = Files.readAllBytes(new File(path).toPath());
        assertEquals(new String(fileBytes, "UTF-8"), new String(expectedBytes, "UTF-8"));
    }

    @Test
    @DisplayName("잘못된 파일 경로에 대한 null 반환 테스트")
    void testFileToByteArrayWithInvalidRequest() throws IOException {
        String path = "./src/main/resources/static/foo.txt";
        byte[] fileBytes = FileUtil.fileToByteArray(path);
        assertNull(fileBytes);
    }
}