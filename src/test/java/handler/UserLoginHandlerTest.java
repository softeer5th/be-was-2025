package handler;

import db.Database;
import db.SessionManager;
import exception.BaseException;
import exception.HttpErrorCode;
import http.*;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class UserLoginHandlerTest {
    private UserLoginHandler userLoginHandler;

    private static final HttpMethod VALID_HTTP_METHOD = HttpMethod.POST;
    private static final HttpMethod INVALID_HTTP_METHOD = HttpMethod.GET;
    private static final String LOGIN_PATH = "/users/login";

    private static final String VALID_CREDENTIALS = "userId=testId&password=test1234!";
    private static final String INVALID_USER = "userId=non&password=test1234!";
    private static final String INVALID_PASSWORD = "userId=testId&password=non";

    @BeforeEach
    void setUp() {
        userLoginHandler = new UserLoginHandler();
        Database.clear();
        SessionManager.clear();
        Database.addUser(new User("testId", "testUser", "test1234!", "test@test.com"));
    }

    private HttpRequestInfo createTestRequest(HttpMethod method, String body) {
        return HttpRequestInfo.forTest(method, LOGIN_PATH, new HashMap<>(), new HashMap<>(), body);
    }

    @Test
    @DisplayName("로그인 성공")
    void testHandleWithValidLoginData() {
        HttpRequestInfo request = createTestRequest(VALID_HTTP_METHOD, VALID_CREDENTIALS);
        HttpResponse response = userLoginHandler.handle(request);

        assertEquals(HttpStatus.FOUND, response.getStatus());
    }

    @Test
    @DisplayName("잘못된 HTTP Method인 경우")
    void testHandleWithInvalidHttpMethod() {
        HttpRequestInfo request = createTestRequest(INVALID_HTTP_METHOD, VALID_CREDENTIALS);

        BaseException exception = assertThrows(BaseException.class, () -> userLoginHandler.handle(request));
        assertEquals(HttpErrorCode.INVALID_HTTP_METHOD.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("일치하는 userId가 없는 경우")
    void testHandleWithUserIdNotFound() {
        HttpRequestInfo request = createTestRequest(VALID_HTTP_METHOD, INVALID_USER);
        HttpResponse response = userLoginHandler.handle(request);

        assertEquals(HttpStatus.SEE_OTHER, response.getStatus());
        assertEquals("/login/failed.html", response.getHeader("Location"));
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않는 경우")
    void testHandleWithIncorrectPassword() {
        HttpRequestInfo request = createTestRequest(VALID_HTTP_METHOD, INVALID_PASSWORD);
        HttpResponse response = userLoginHandler.handle(request);

        assertEquals(HttpStatus.SEE_OTHER, response.getStatus());
        assertEquals("/login/failed.html", response.getHeader("Location"));
    }
}
