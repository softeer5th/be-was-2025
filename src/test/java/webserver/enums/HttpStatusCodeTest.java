package webserver.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.exception.InternalServerError;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpStatusCodeTest {

    @Test
    @DisplayName("없는 상태 코드일 경우 Internal Server Error 예외 발생")
    void of_InvalidCode() {
        assertThatThrownBy(() -> HttpStatusCode.of(509))
                .isInstanceOf(InternalServerError.class);
        assertThatThrownBy(() -> HttpStatusCode.of(104))
                .isInstanceOf(InternalServerError.class);
    }


}