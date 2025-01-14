package webserver.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.exception.NotImplemented;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpVersionTest {
    @Test
    @DisplayName("String 값에 해당하는 HttpVersion 반환")
    void of() {
        assertThat(HttpVersion.of("HTTP/0.9")).isEqualTo(HttpVersion.HTTP_0_9);
        assertThat(HttpVersion.of("HTTP/1.0")).isEqualTo(HttpVersion.HTTP_1_0);
        assertThat(HttpVersion.of("HTTP/1.1")).isEqualTo(HttpVersion.HTTP_1_1);
        assertThat(HttpVersion.of("HTTP/2.0")).isEqualTo(HttpVersion.HTTP_2_0);
        assertThat(HttpVersion.of("HTTP/3.0")).isEqualTo(HttpVersion.HTTP_3_0);
    }

    @Test
    @DisplayName("존재하지 않는 HttpVersion을 입력받을 경우 NotImplemented 예외 발생")
    void of_InvalidVersion() {
        assertThatThrownBy(() -> HttpVersion.of("INVALID"))
                .isInstanceOf(NotImplemented.class);
        assertThatThrownBy(() -> HttpVersion.of(null))
                .isInstanceOf(NotImplemented.class);
    }
}