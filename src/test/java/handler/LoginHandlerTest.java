package handler;

import domain.User;
import domain.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.exception.BadRequest;
import webserver.request.HttpRequest;
import webserver.session.HttpSession;

import java.util.Optional;

import static enums.PageMappingPath.INDEX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class LoginHandlerTest {

    private UserDao userDao;
    private LoginHandler loginHandler;

    @BeforeEach
    void setUp() {
        userDao = mock(UserDao.class);
        loginHandler = new LoginHandler(userDao);
    }

    @Test
    @DisplayName("로그인 페이지 로드")
    void test1() {
        // given
        var request = mock(HttpRequest.class);

        // when
        var response = loginHandler.handleGet(request);

        // then
        assertThat(response.getStatusCode().statusCode).isEqualTo(200);
    }

    @Test
    @DisplayName("로그인 성공 후 메인페이지 이동")
    void test2() {
        // given
        var request = mock(HttpRequest.class);
        var session = mock(HttpSession.class);
        User user = mock(User.class);
        when(request.getBody(LoginHandler.LoginRequest.class)).thenReturn(Optional.of(new LoginHandler.LoginRequest("validUser", "validPass")));
        when(userDao.findUserById("validUser")).thenReturn(Optional.of(user));
        when(user.isPasswordCorrect("validPass")).thenReturn(true);
        when(request.getSession()).thenReturn(session);

        // when
        var response = loginHandler.handlePost(request);

        // then
        assertThat(response.getStatusCode().statusCode).isEqualTo(302);
        assertThat(response.getHeaders().getHeader("Location")).isEqualTo(INDEX.path);
        verify(session).set(HttpSession.USER_KEY, user);
    }

    @Test
    @DisplayName("없는 사용자로 로그인 시 401")
    void test3() {
        // given
        var request = mock(HttpRequest.class);
        when(request.getBody(LoginHandler.LoginRequest.class)).thenReturn(Optional.of(new LoginHandler.LoginRequest("invalidUser", "invalidPass")));
        when(userDao.findUserById("invalidUser")).thenReturn(Optional.empty());

        // when
        var response = loginHandler.handlePost(request);

        // then
        assertThat(response.getStatusCode().statusCode).isEqualTo(401);
    }

    @Test
    @DisplayName("id 길이가 맞지 않는 경우 400")
    void test4() {
        // given
        var request = mock(HttpRequest.class);
        when(request.getBody(LoginHandler.LoginRequest.class))
                .thenReturn(Optional.of(new LoginHandler.LoginRequest("ab", "validPass")));

        // when & then
        assertThrows(BadRequest.class, () -> loginHandler.handlePost(request));
    }

    @Test
    @DisplayName("password가 다른경우 401")
    void test5() {
        // given
        var request = mock(HttpRequest.class);
        var user = mock(User.class);
        when(userDao.findUserById("validUser"))
                .thenReturn(Optional.of(user));
        when(user.isPasswordCorrect("invalidPass"))
                .thenReturn(false);
        when(request.getBody(LoginHandler.LoginRequest.class))
                .thenReturn(Optional.of(new LoginHandler.LoginRequest("validUser", "invalidPass")));

        // when
        var response = loginHandler.handlePost(request);

        // then
        assertThat(response.getStatusCode().statusCode).isEqualTo(401);
    }


}