package handler;

import static org.junit.jupiter.api.Assertions.*;

import exception.BaseException;
import exception.HttpErrorCode;
import http.HttpMethod;
import http.HttpRequestInfo;
import http.HttpResponse;
import http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserRegisterHandlerTest {

    private final UserRegisterHandler userRegisterHandler = new UserRegisterHandler();

    private static final String VALID_REQUEST_PATH = "/users/register?userId=dbfl0461&nickname=yulee&password=qwer1234!&email=dbfl0461@gmail.com";
    private static final String INVALID_REQUEST_PATH = "/users/register?userId=yulee";

    @Test
    @DisplayName("회원가입 성공")
    void testHandleWithValidUserData() {
        HttpRequestInfo httpRequestInfo = new HttpRequestInfo(HttpMethod.GET, VALID_REQUEST_PATH);
        HttpResponse response = userRegisterHandler.handle(httpRequestInfo);

        assertEquals(HttpStatus.FOUND, response.getStatus());
    }

    @Test
    @DisplayName("회원가입 실패")
    void testHandleWithInvalidQueryParams() {
        HttpRequestInfo httpRequestInfo = new HttpRequestInfo(HttpMethod.GET, INVALID_REQUEST_PATH);

        BaseException baseException = assertThrows(BaseException.class, () -> userRegisterHandler.handle(httpRequestInfo));
        assertEquals(baseException.getMessage(), HttpErrorCode.INVALID_QUERY_PARAM.getMessage());
    }
}
