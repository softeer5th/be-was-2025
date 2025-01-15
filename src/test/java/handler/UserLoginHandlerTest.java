package handler;

import db.Database;
import exception.BaseException;
import exception.HttpErrorCode;
import http.HttpMethod;
import http.HttpRequestInfo;
import http.HttpResponse;
import http.HttpStatus;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserLoginHandlerTest {
    private final UserLoginHandler userLoginHandler = new UserLoginHandler();

    private static final HttpMethod VALID_HTTP_METHOD = HttpMethod.POST;
    private static final HttpMethod INVALID_HTTP_METHOD = HttpMethod.GET;
    private static final String VALID_REQUEST_PATH = "/users/login";
    private static final String VALID_QUERY_PARAM = "userId=yulee&password=1234";
    private static final String NO_USER_PARAM = "userId=non&password=1234";
    private static final String INVALID_PASSWORD_PARAM = "userId=yulee&password=non";


    @BeforeEach
    void setUp() {
        Database.addUser(new User("yulee", "yulee", "1234", "yulee@example.com"));
    }

    @Test
    @DisplayName("로그인 성공")
    void testHandleWithValidLoginData() {
        HttpRequestInfo httpRequestInfo = new HttpRequestInfo(VALID_HTTP_METHOD, VALID_REQUEST_PATH, VALID_QUERY_PARAM);
        HttpResponse response = userLoginHandler.handle(httpRequestInfo);

        assertEquals(HttpStatus.FOUND, response.getStatus());
    }

    @Test
    @DisplayName("잘못된 HTTP Method인 경우")
    void testHandleWithInvalidHttpMethod() {
        HttpRequestInfo httpRequestInfo = new HttpRequestInfo(INVALID_HTTP_METHOD, VALID_REQUEST_PATH, VALID_QUERY_PARAM);

        BaseException baseException = assertThrows(BaseException.class, () -> userLoginHandler.handle(httpRequestInfo));
        assertEquals(baseException.getMessage(), HttpErrorCode.INVALID_HTTP_METHOD.getMessage());
    }

    @Test
    @DisplayName("일치하는 userId가 없는 경우")
    void testHandleWithUserIdNotFound() {
        HttpRequestInfo httpRequestInfo = new HttpRequestInfo(VALID_HTTP_METHOD, VALID_REQUEST_PATH, NO_USER_PARAM);
        HttpResponse response = userLoginHandler.handle(httpRequestInfo);

        assertEquals(HttpStatus.FOUND, response.getStatus());
        assertEquals("/login/failed.html", response.getHeader("Location"));
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않는 경우")
    void testHandleWithIncorrectPassword() {
        HttpRequestInfo httpRequestInfo = new HttpRequestInfo(VALID_HTTP_METHOD, VALID_REQUEST_PATH, INVALID_PASSWORD_PARAM);
        HttpResponse response = userLoginHandler.handle(httpRequestInfo);

        assertEquals(HttpStatus.FOUND, response.getStatus());
        assertEquals("/login/failed.html", response.getHeader("Location"));
    }

}
