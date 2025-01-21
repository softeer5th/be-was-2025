package webserver.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContentTypeTest {
    @Test
    @DisplayName("파일 확장자에 따른 Content Type 반환")
    void of() {
        assertThat(ContentType.of("txt")).isEqualTo(ContentType.TEXT_PLAIN);
        assertThat(ContentType.of("html")).isEqualTo(ContentType.TEXT_HTML);
        assertThat(ContentType.of("css")).isEqualTo(ContentType.TEXT_CSS);
        assertThat(ContentType.of("jpeg")).isEqualTo(ContentType.IMAGE_JPEG);
        assertThat(ContentType.of("jpg")).isEqualTo(ContentType.IMAGE_JPG);
        assertThat(ContentType.of("png")).isEqualTo(ContentType.IMAGE_PNG);
        assertThat(ContentType.of("gif")).isEqualTo(ContentType.IMAGE_GIF);
        assertThat(ContentType.of("svg")).isEqualTo(ContentType.IMAGE_SVG);
        assertThat(ContentType.of("ico")).isEqualTo(ContentType.IMAGE_ICO);
        assertThat(ContentType.of("js")).isEqualTo(ContentType.APPLICATION_JAVASCRIPT);
        assertThat(ContentType.of("json")).isEqualTo(ContentType.APPLICATION_JSON);
        assertThat(ContentType.of("xml")).isEqualTo(ContentType.APPLICATION_XML);
        assertThat(ContentType.of("bin")).isEqualTo(ContentType.APPLICATION_OCTET_STREAM);
    }

    @Test
    @DisplayName("파일 확장자에 따른 Content Type 반환 - 없는 확장자")
    void of_NotExistExtension() {
        assertThat(ContentType.of("hwp")).isEqualTo(ContentType.APPLICATION_OCTET_STREAM);
    }

    @Test
    @DisplayName("mime type 문자열 비교")
    void equals() {
        assertThat(ContentType.TEXT_PLAIN.equals("text/plain")).isTrue();
        assertThat(ContentType.TEXT_HTML.equals("text/plaine")).isFalse();
        assertThat(ContentType.APPLICATION_X_WWW_FORM_URLENCODED.equals("application/x-www-form-urlencoded")).isTrue();
    }

}