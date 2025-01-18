package webserver.handler;

import db.Database;
import handler.RegistrationHandler;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.enums.HttpHeader;
import webserver.enums.HttpStatusCode;
import webserver.enums.PageMappingPath;
import webserver.exception.Conflict;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RegistrationHandlerTest {
    private Database database;
    private RegistrationHandler handler;

    @BeforeEach
    void setUp() {
        database = new Database();
        handler = new RegistrationHandler(database);
    }

    @Test
    @DisplayName("회원가입 성공 후 메인페이지 이동")
    void handlePost_addUser() {
        // given
        var request = mock(HttpRequest.class);
        when(request.getBodyAsMap()).thenReturn(Optional.of(Map.of(
                "userId", "id",
                "password", "1234",
                "name", "name",
                "email", "example@example.com"
        )));

        // when
        HttpResponse response = handler.handlePost(request);

        // then
        User user = database.findUserById("id").get();
        assertThat(user).isNotNull();
        assertThat(user.getUserId()).isEqualTo("id");
        assertThat(user.isPasswordCorrect("1234")).isTrue();
        assertThat(user.getName()).isEqualTo("name");
        assertThat(user.getEmail()).isEqualTo("example@example.com");
        // 회원가입 완료 후 INDEX 페이지로 리다이렉트
        assertThat(response.getStatusCode()).matches(HttpStatusCode::isRedirection);
        assertThat(response.getHeaders().getHeader(HttpHeader.LOCATION.value)).isEqualTo(PageMappingPath.INDEX.path);
    }

    @Test
    @DisplayName("중복된 아이디로 회원가입 시도 시 실패")
    void handlePost_duplicateUser() {
        // given
        var request = mock(HttpRequest.class);
        when(request.getBodyAsMap()).thenReturn(Optional.of(Map.of(
                "userId", "id",
                "password", "1234",
                "name", "name",
                "email", "example@example.com"
        )));
        database.saveUser(new User("id", "123423", "John", "abc2@example2.com"));

        // when & then
        assertThatThrownBy(() -> handler.handlePost(request))
                .isInstanceOf(Conflict.class);
    }
}