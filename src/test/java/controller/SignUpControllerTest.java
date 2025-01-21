package controller;

import model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;
import webserver.httpserver.StatusCode;

import java.io.File;
import java.io.IOException;

import static db.Database.findUserById;
import static org.mockito.Mockito.*;
import static utils.FileUtils.getFile;

class SignUpControllerTest {

    @Test
    @DisplayName("회원가입 성공 테스트")
    public void createUserSuccessTest() throws IOException {
        // given
        HttpRequest request = mock(HttpRequest.class);
        HttpResponse response = mock(HttpResponse.class);

        when(request.getParameter(User.USER_ID)).thenReturn("testUser");
        when(request.getParameter(User.PASSWORD)).thenReturn("testPassword");
        when(request.getParameter(User.USERNAME)).thenReturn("testName");
        when(request.getParameter(User.EMAIL)).thenReturn("test@example.com");

        SignUpController signUpController = new SignUpController();

        // when
        signUpController.createUser(request, response);

        // then
        User testUser = findUserById("testUser");
        Assertions.assertThat(testUser).isNotNull();
        Assertions.assertThat(testUser.getPassword()).isEqualTo("testPassword");
        Assertions.assertThat(testUser.getName()).isEqualTo("testName");
        Assertions.assertThat(testUser.getEmail()).isEqualTo("test@example.com");
        verify(response).setLocation("/");
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 필수 파라미터 누락")
    public void createUserFailureTest() throws IOException {
        // given
        HttpRequest request = mock(HttpRequest.class);
        HttpResponse response = mock(HttpResponse.class);

        when(request.getParameter(User.USER_ID)).thenReturn("testUser");
        when(request.getParameter(User.PASSWORD)).thenReturn("testPassword");
        when(request.getParameter(User.USERNAME)).thenReturn("testName");
        when(request.getParameter(User.EMAIL)).thenReturn(null);

        SignUpController signUpController = new SignUpController();

        // when
        signUpController.createUser(request, response);

        // then
        User testUser = findUserById("testUser");
        Assertions.assertThat(testUser).isNull();
        verify(response).setLocation("/registration");
    }

    @Test
    @DisplayName("회원가입 페이지 로드 테스트")
    public void registerPageTest() throws IOException {
        // given
        HttpResponse response = mock(HttpResponse.class);
        SignUpController signUpController = new SignUpController();

        // when
        signUpController.registerPage(response);

        // then
        verify(response).setStatusCode(StatusCode.OK);
        verify(response).setHeader("Content-Type", "text/html; charset=utf-8");

        File expectedFile = new File("src/main/resources/static/registration/index.html");
        byte[] expectedBody = getFile(expectedFile);
        verify(response).setBody(expectedBody);
    }

    @Test
    @DisplayName("회원가입 성공 페이지 로드 테스트")
    public void signUpSuccessTest() throws IOException {
        // given
        HttpResponse response = mock(HttpResponse.class);
        SignUpController signUpController = new SignUpController();

        // when
        signUpController.signUpSuccess(response);

        // then
        verify(response).setStatusCode(StatusCode.OK);
        verify(response).setHeader("Content-Type", "text/html;charset=utf-8");

        File expectedFile = new File("src/main/resources/static/registration/success.html");
        byte[] expectedBody = getFile(expectedFile);
        verify(response).setBody(expectedBody);
    }
}
