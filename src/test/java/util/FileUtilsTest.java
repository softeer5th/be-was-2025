package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.exception.NoSuchPathException;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

class FileUtilsTest {

    @Test
    @DisplayName("파일 찾기 테스트")
    public void test1() throws IOException {
        File file = FileUtils.findFile("/");

        assertThat(file).isNotNull();
        assertThat(file.isFile()).isTrue();
        assertThat(file.isDirectory()).isFalse();

        assertThat(file.getPath()).isEqualTo("./src/main/resources/static/index.html");
    }

    @Test
    @DisplayName("파일 찾기 실패 테스트")
    public void test2() throws IOException {
        String path = "/notfoundpath";

        assertThatThrownBy(() -> {
            File file = FileUtils.findFile(path);
        }).isInstanceOf(NoSuchPathException.class);
    }

    @Test
    @DisplayName("파일 확장자 찾기 테스트")
    public void test3() throws IOException {
        String path = "/";

        File file = FileUtils.findFile(path);

        assertThat(file).isNotNull();

        String extension = FileUtils.getExtension(file);

        assertThat(extension).isNotNull();
        assertThat(extension).isEqualTo("html");
    }

    @Test
    @DisplayName("파일 바이트로 변환 테스트")
    public void test4() throws IOException {
        String path = "/";

        File file = FileUtils.findFile(path);

        assertThat(file).isNotNull();

        byte[] body = FileUtils.convertToByte(file);

        assertThat(body).isNotEmpty();
    }

    @Test
    @DisplayName("파일 문자열로 변환 테스트")
    public void test5() throws IOException {
        String path = "/";

        File file = FileUtils.findFile(path);

        assertThat(file).isNotNull();

        String body = FileUtils.convertToString(file);

        assertThat(body).isNotEmpty();
    }
}