package controller;

import db.Database;
import model.User;
import org.junit.jupiter.api.*;
import webserver.httpserver.*;
import webserver.httpserver.header.*;
import wasframework.HttpSession;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserControllerTest {

    private UserController userController;
    private HttpResponse response;
    private CookieFactory cookieFactory;

    @BeforeEach
    void setUp() {
        userController = new UserController();
        response = new HttpResponse();
        response.setProtocol("HTTP/1.1");
        cookieFactory = new CookieFactory();

        Database.clear();
        HttpSession.clear();
    }

    @AfterEach
    void tearDown() {
        Database.clear();
        HttpSession.clear();
    }

    @Test
    @DisplayName("GET /login - 로그인 페이지 요청 테스트")
    void testLoginPage() throws IOException {
        // when
        userController.loginPage(response);

        // then
        assertThat(response.getStatusCode()).isEqualTo(StatusCode.OK);
        assertThat(response.getHeader("Content-Type")).isEqualTo("text/html; charset=utf-8");
        assertThat(response.getBody()).isNotNull();

        String bodyContent = new String(response.getBody(), StandardCharsets.UTF_8);
        assertThat(bodyContent).contains("<!DOCTYPE html");
    }

    @Test
    @DisplayName("POST /login - 로그인 성공 시 홈으로 리다이렉션")
    void testLoginSuccess() throws NoSuchFieldException, IllegalAccessException {
        // given
        User user = new User("testuser", "password", "Test User", "test@example.com");
        Database.addUser(user);

        HttpRequest request = new HttpRequest.Builder()
                .method(HttpMethod.POST)
                .uri("/login")
                .addParameter(User.USER_ID, "testuser")
                .addParameter(User.PASSWORD, "password")
                .build();

        // when
        userController.login(request, response);

        // then
        assertThat(response.getStatusCode()).isEqualTo(StatusCode.SEE_OTHER);
        assertThat(response.getHeader("Location")).isEqualTo("/");

        SetCookie setCookie = extractFirstSetCookie(response);
        assertThat(setCookie).isNotNull();
        String cookieString = setCookie.toString();
        assertThat(cookieString).contains("sid=");

        String sessionId = parseSessionId(setCookie.getCookie());
        assertThat(sessionId).isNotNull();
        assertThat(HttpSession.get(sessionId)).isEqualTo("testuser");
    }

    @Test
    @DisplayName("POST /login - 존재하지 않는 유저로 로그인 시 실패")
    void testLoginFail_NoUser() {
        // given
        HttpRequest request = new HttpRequest.Builder()
                .method(HttpMethod.POST)
                .uri("/login")
                .addParameter(User.USER_ID, "nonexistent")
                .addParameter(User.PASSWORD, "wrongpassword")
                .build();

        // when
        userController.login(request, response);

        // then
        assertThat(response.getStatusCode()).isEqualTo(StatusCode.SEE_OTHER);
        assertThat(response.getHeader("Location")).isEqualTo("/user/login_failed");
    }

    @Test
    @DisplayName("POST /login - 파라미터 누락 시 로그인 실패")
    void testLoginFail_NoParameter() {
        // given
        HttpRequest request = new HttpRequest.Builder()
                .method(HttpMethod.POST)
                .uri("/login")
                .build();

        // when
        userController.login(request, response);

        // then
        assertThat(response.getStatusCode()).isEqualTo(StatusCode.SEE_OTHER);
        assertThat(response.getHeader("Location")).isEqualTo("/user/login_failed");
    }

    @Test
    @DisplayName("GET /user/login_failed - 로그인 실패 페이지 서빙 테스트")
    void testLoginFailedPage() throws IOException {
        // when
        userController.loginFailed(response);

        // then
        assertThat(response.getStatusCode()).isEqualTo(StatusCode.OK);
        assertThat(response.getHeader("Content-Type")).isEqualTo("text/html; charset=utf-8");
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("POST /logout - 정상 로그아웃 시 홈으로 리다이렉트 및 세션 만료")
    void testLogout() throws NoSuchFieldException, IllegalAccessException {
        // given
        String userId = "tester";
        User user = new User("tester", "pw", "Tester", "tester@example.com");
        Database.addUser(user);
        String sessionUuid = UUID.randomUUID().toString();
        HttpSession.put(sessionUuid, userId);

        Cookie cookie = cookieFactory.create("sid=" + sessionUuid);

        HttpRequest request = new HttpRequest.Builder()
                .method(HttpMethod.POST)
                .uri("/logout")
                .cookie(cookie)
                .build();

        // when
        userController.logout(request, response);

        // then
        assertThat(response.getHeader("Location")).isEqualTo("/");
        assertThat(response.getStatusCode()).isEqualTo(StatusCode.SEE_OTHER);

        assertThat(HttpSession.get(sessionUuid)).isNull();

        SetCookie setCookie = extractFirstSetCookie(response);
        assertThat(setCookie).isNotNull();
        String cookieString = setCookie.toString();
        assertThat(cookieString).contains("Max-Age=0");
    }

    private SetCookie extractFirstSetCookie(HttpResponse response) throws IllegalAccessException, NoSuchFieldException {
        Field cookies = response.getClass().getDeclaredField("cookies");
        cookies.setAccessible(true);
        List<SetCookie> o = (List<SetCookie>) cookies.get(response);
        return o.get(0);
    }

    private String parseSessionId(String cookieValue) {
        if (cookieValue == null) return null;
        String[] parts = cookieValue.split("=");
        return parts.length > 1 ? parts[1] : null;
    }
}
