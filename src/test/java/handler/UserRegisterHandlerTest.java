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

    private static final HttpMethod VALID_HTTP_METHOD = HttpMethod.POST;
    private static final HttpMethod INVALID_HTTP_METHOD = HttpMethod.GET;
    private static final String VALID_REQUEST_PATH = "/users/register";
    private static final String VALID_QUERY_PARAM = "userId=yulee&nickname=uri&password=1234&email=1234%40example.com";

    @Test
    @DisplayName("회원가입 성공")
    void testHandleWithValidUserData() {
        HttpRequestInfo httpRequestInfo = new HttpRequestInfo(VALID_HTTP_METHOD, VALID_REQUEST_PATH, VALID_QUERY_PARAM);
        HttpResponse response = userRegisterHandler.handle(httpRequestInfo);

        assertEquals(HttpStatus.FOUND, response.getStatus());
    }

    @Test
    @DisplayName("잘못된 HTTP Method인 경우")
    void testHandleWithInvalidHttpMethod() {
        HttpRequestInfo httpRequestInfo = new HttpRequestInfo(INVALID_HTTP_METHOD, VALID_REQUEST_PATH, VALID_QUERY_PARAM);

        BaseException baseException = assertThrows(BaseException.class, () -> userRegisterHandler.handle(httpRequestInfo));
        assertEquals(baseException.getMessage(), HttpErrorCode.INVALID_HTTP_METHOD.getMessage());
    }
}
