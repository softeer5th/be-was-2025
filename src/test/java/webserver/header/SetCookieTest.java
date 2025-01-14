package webserver.header;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SetCookieTest {

    @Test
    @DisplayName("httpOnly 기본값 확인")
    void httpOnly() {
        SetCookie setCookie = new SetCookie("name", "value");
        assertThat(setCookie.isHttpOnly()).isTrue();
    }
}