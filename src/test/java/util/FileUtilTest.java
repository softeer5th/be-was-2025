package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class FileUtilTest {

    @Test
    @DisplayName("파일 확장자 추출")
    void getFileExtension() {
        String fileName = "test.txt";
        String result = FileUtil.getFileExtension(fileName);
        assertThat(result).isEqualTo("txt");
    }

    @Test
    @DisplayName("경로 합치기")
    void joinPath() {
        String separator = File.separator;
        String result = FileUtil.joinPath("a", "b", "c");
        assertThat(result).isEqualTo("a" + separator + "b" + separator + "c");
    }
}