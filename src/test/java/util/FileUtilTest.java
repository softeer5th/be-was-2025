package util;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FileUtilTest {
    @Test
    void testFileToByteArrayWithValidRequest() throws IOException {
        String path = "./src/main/resources/static/index.html";
        byte[] fileBytes = FileUtil.fileToByteArray(path);
        byte[] result = Files.readAllBytes(new File(path).toPath());
        assertEquals(new String(fileBytes, "UTF-8"), new String(result, "UTF-8"));
    }

    @Test
    void testFileToByteArrayWithInvalidRequest() throws IOException {
        String path = "./src/main/resources/static/foo.txt";
        byte[] fileBytes = FileUtil.fileToByteArray(path);
        assertNull(fileBytes);
    }
}
