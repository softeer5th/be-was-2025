package util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileReaderTest {
    private static final String STATIC_FILE_PATH = "./src/test/resources/static";

    @Test
    @DisplayName("파일을 읽는다.")
    void readFile() {
        // given
        byte[] test = "test".getBytes();
        // when

        Optional<byte[]> file = FileReader.readFile(STATIC_FILE_PATH + "/test.txt");

        // then
        assertTrue(file.isPresent());
        assertArrayEquals(test, file.get());
    }

    @Test
    @DisplayName("존재하지 않는 파일을 읽을 경우 빈 Optional을 반환한다.")
    void readFile_InvalidFile() {
        // when
        Optional<byte[]> file = FileReader.readFile(STATIC_FILE_PATH + "/invalid.txt");

        // then
        assertTrue(file.isEmpty());
    }

    @Test
    @DisplayName("파일을 문자열로 읽는다")
    void readFileAsString(){
        String test = "test";

        Optional<String> file = FileReader.readFileAsString(STATIC_FILE_PATH + "/test.txt");

        assertTrue(file.isPresent());
        Assertions.assertThat(file.get())
                .isEqualTo(test);
    }

    @Test
    @DisplayName("존재하지 않는 파일을 읽을 경우 빈 Optional을 반환한다.")
    void readFileAsString_InvalidFile() {
        Optional<String> file = FileReader.readFileAsString(STATIC_FILE_PATH + "/invalid.txt");
        assertTrue(file.isEmpty());
    }
}