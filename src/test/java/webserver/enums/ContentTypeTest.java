package webserver.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContentTypeTest {
    @Test
    @DisplayName("파일 확장자에 따른 Content Type 반환")
    void of() {
        assertThat(ContentType.fromExtension("txt")).isEqualTo(ContentType.TEXT_PLAIN);
        assertThat(ContentType.fromExtension("html")).isEqualTo(ContentType.TEXT_HTML);
        assertThat(ContentType.fromExtension("css")).isEqualTo(ContentType.TEXT_CSS);
        assertThat(ContentType.fromExtension("jpeg")).isEqualTo(ContentType.IMAGE_JPEG);
        assertThat(ContentType.fromExtension("jpg")).isEqualTo(ContentType.IMAGE_JPG);
        assertThat(ContentType.fromExtension("png")).isEqualTo(ContentType.IMAGE_PNG);
        assertThat(ContentType.fromExtension("gif")).isEqualTo(ContentType.IMAGE_GIF);
        assertThat(ContentType.fromExtension("svg")).isEqualTo(ContentType.IMAGE_SVG);
        assertThat(ContentType.fromExtension("ico")).isEqualTo(ContentType.IMAGE_ICO);
        assertThat(ContentType.fromExtension("js")).isEqualTo(ContentType.APPLICATION_JAVASCRIPT);
        assertThat(ContentType.fromExtension("json")).isEqualTo(ContentType.APPLICATION_JSON);
        assertThat(ContentType.fromExtension("xml")).isEqualTo(ContentType.APPLICATION_XML);
        assertThat(ContentType.fromExtension("bin")).isEqualTo(ContentType.APPLICATION_OCTET_STREAM);
    }

    @Test
    @DisplayName("파일 확장자에 따른 Content Type 반환 - 없는 확장자")
    void of_NotExistExtension() {
        assertThat(ContentType.fromExtension("hwp")).isEqualTo(ContentType.APPLICATION_OCTET_STREAM);
    }

    @Test
    @DisplayName("mime type 문자열 비교")
    void equals() {
        assertThat(ContentType.TEXT_PLAIN.equals("text/plain")).isTrue();
        assertThat(ContentType.TEXT_HTML.equals("text/plaine")).isFalse();
        assertThat(ContentType.APPLICATION_X_WWW_FORM_URLENCODED.equals("application/x-www-form-urlencoded")).isTrue();
    }

}