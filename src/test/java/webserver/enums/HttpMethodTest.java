package webserver.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.exception.NotImplemented;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpMethodTest {
    @Test
    @DisplayName("String 값에 해당하는 HttpMethod 반환")
    void of() {
        assertThat(HttpMethod.of("GET")).isEqualTo(HttpMethod.GET);
        assertThat(HttpMethod.of("POST")).isEqualTo(HttpMethod.POST);
        assertThat(HttpMethod.of("PUT")).isEqualTo(HttpMethod.PUT);
        assertThat(HttpMethod.of("DELETE")).isEqualTo(HttpMethod.DELETE);
        assertThat(HttpMethod.of("CONNECT")).isEqualTo(HttpMethod.CONNECT);
        assertThat(HttpMethod.of("OPTIONS")).isEqualTo(HttpMethod.OPTIONS);
        assertThat(HttpMethod.of("TRACE")).isEqualTo(HttpMethod.TRACE);
        assertThat(HttpMethod.of("PATCH")).isEqualTo(HttpMethod.PATCH);
    }

    @Test
    @DisplayName("존재하지 않는 HttpMethod을 입력받을 경우 NotImplemented 예외 발생")
    void of_InvalidMethod() {
        assertThatThrownBy(() -> HttpMethod.of("INVALID"))
                .isInstanceOf(NotImplemented.class);
        assertThatThrownBy(() -> HttpMethod.of(null))
                .isInstanceOf(NotImplemented.class);
    }

    @Test
    @DisplayName("대소문자가 다른 HttpMethod을 입력받을 경우 NotImplemented 예외 발생")
    void of_CaseSensitiveMethod() {
        assertThatThrownBy(() -> HttpMethod.of("Get"))
                .isInstanceOf(NotImplemented.class);
        assertThatThrownBy(() -> HttpMethod.of("Post"))
                .isInstanceOf(NotImplemented.class);
    }
}