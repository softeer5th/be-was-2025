package handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import user.User;
import user.UserDao;
import webserver.enums.HttpHeader;
import webserver.enums.HttpStatusCode;
import webserver.enums.PageMappingPath;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RegistrationHandlerTest {
    private UserDao userDao;
    private RegistrationHandler handler;

    @BeforeEach
    void setUp() {
        userDao = mock(UserDao.class);
        handler = new RegistrationHandler(userDao);
    }

    @Test
    @DisplayName("회원가입 성공 후 메인페이지 이동")
    void handlePost_addUser() {
        // given
        var request = mock(HttpRequest.class);
        when(request.getBody(any())).thenReturn(Optional.of(new RegistrationHandler
                .RegistrationRequest("id", "1234", "name", "example@example.com")));
        var userCaptor = ArgumentCaptor.forClass(User.class);
        when(userDao.saveUser(userCaptor.capture())).thenReturn(true);

        // when
        HttpResponse response = handler.handlePost(request);

        // then
        User user = userCaptor.getValue();
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
    @DisplayName("중복된 아이디로 회원가입 시도 시 Conflict")
    void handlePost_duplicateUser() {
        // given
        var request = mock(HttpRequest.class);
        when(request.getBody(any())).thenReturn(Optional.of(new RegistrationHandler
                .RegistrationRequest("id", "1234", "name", "example@example.com")));
        when(userDao.findUserById("id")).thenReturn(Optional.of(User.create("id", "123423", "John", "abc2@example2.com")));


        // when
        var response = handler.handlePost(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.CONFLICT);
    }
}