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

    private static final HttpMethod HTTP_METHOD = HttpMethod.POST;
    private static final String VALID_REQUEST_PATH = "/users/register";
    private static final String VALID_QUERY_PARAM = "userId=yulee&nickname=uri&password=1234&email=1234%40example.com";
    private static final String INVALID_QUERY_PARAM = "userId=yulee&nickname=uri";

    @Test
    @DisplayName("회원가입 성공")
    void testHandleWithValidUserData() {
        HttpRequestInfo httpRequestInfo = new HttpRequestInfo(HTTP_METHOD, VALID_REQUEST_PATH, VALID_QUERY_PARAM);
        HttpResponse response = userRegisterHandler.handle(httpRequestInfo);

        assertEquals(HttpStatus.FOUND, response.getStatus());
    }

    @Test
    @DisplayName("잘못된 쿼리문인 경우")
    void testHandleWithInvalidQueryParam() {
        HttpRequestInfo httpRequestInfo = new HttpRequestInfo(HTTP_METHOD, VALID_REQUEST_PATH, INVALID_QUERY_PARAM);

        BaseException baseException = assertThrows(BaseException.class, () -> userRegisterHandler.handle(httpRequestInfo));
        assertEquals(baseException.getMessage(), HttpErrorCode.INVALID_QUERY_PARAM.getMessage());
    }

    @Test
    @DisplayName("잘못된 HTTP Method인 경우")
    void testHandleWithInvalidHttpMethod() {
        HttpRequestInfo httpRequestInfo = new HttpRequestInfo(HttpMethod.GET, VALID_REQUEST_PATH, VALID_QUERY_PARAM);

        BaseException baseException = assertThrows(BaseException.class, () -> userRegisterHandler.handle(httpRequestInfo));
        assertEquals(baseException.getMessage(), HttpErrorCode.INVALID_HTTP_METHOD.getMessage());
    }
}
