package webserver.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.exception.BadRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpStatusCodeTest {
    @Test
    @DisplayName("상태 코드에 해당하는 HttpStatusCode 반환")
    void of() {
        assertThat(HttpStatusCode.of(200)).isEqualTo(HttpStatusCode.OK);
        assertThat(HttpStatusCode.of(201)).isEqualTo(HttpStatusCode.CREATED);
        assertThat(HttpStatusCode.of(204)).isEqualTo(HttpStatusCode.NO_CONTENT);
        assertThat(HttpStatusCode.of(301)).isEqualTo(HttpStatusCode.MOVED_PERMANENTLY);
        assertThat(HttpStatusCode.of(302)).isEqualTo(HttpStatusCode.FOUND);
        assertThat(HttpStatusCode.of(304)).isEqualTo(HttpStatusCode.NOT_MODIFIED);
        assertThat(HttpStatusCode.of(400)).isEqualTo(HttpStatusCode.BAD_REQUEST);
        assertThat(HttpStatusCode.of(401)).isEqualTo(HttpStatusCode.UNAUTHORIZED);
        assertThat(HttpStatusCode.of(403)).isEqualTo(HttpStatusCode.FORBIDDEN);
        assertThat(HttpStatusCode.of(404)).isEqualTo(HttpStatusCode.NOT_FOUND);
        assertThat(HttpStatusCode.of(500)).isEqualTo(HttpStatusCode.INTERNAL_SERVER_ERROR);
        assertThat(HttpStatusCode.of(501)).isEqualTo(HttpStatusCode.NOT_IMPLEMENTED);
    }

    @Test
    @DisplayName("100~599 범위 밖의 값을 입력받을 경우 BadRequest 예외 발생")
    void of_InvalidCode() {
        assertThatThrownBy(() -> HttpStatusCode.of(600))
                .isInstanceOf(BadRequest.class);
        assertThatThrownBy(() -> HttpStatusCode.of(99))
                .isInstanceOf(BadRequest.class);
    }
    

}