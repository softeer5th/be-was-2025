package servlet;

import controller.SignUpController;
import db.Database;
import model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;
import webserver.httpserver.StatusCode;

import java.io.IOException;

import static db.Database.findUserById;
import static org.mockito.Mockito.*;

class CreateServletTest {

    @Test
    @DisplayName("회원가입 테스트")
    public void createUserTest() throws IOException {
        HttpRequest request = mock(HttpRequest.class);
        HttpResponse response = mock(HttpResponse.class);

        when(request.getParameter("userId")).thenReturn("testUser");
        when(request.getParameter("password")).thenReturn("testPassword");
        when(request.getParameter("name")).thenReturn("testName");

        SignUpController signUpController = new SignUpController();
        signUpController.createUser(request, response);

        User testUser = findUserById("testUser");
        Assertions.assertThat(testUser.getPassword()).isEqualTo("testPassword");
        Assertions.assertThat(testUser.getName()).isEqualTo("testName");

        verify(response).setLocation("/");
    }
}