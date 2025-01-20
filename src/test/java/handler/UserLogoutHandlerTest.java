package handler;

import db.SessionManager;
import exception.BaseException;
import exception.HttpErrorCode;
import exception.UserErrorCode;
import http.HttpMethod;
import http.HttpRequestInfo;
import http.HttpResponse;
import http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserLogoutHandlerTest {
    private final UserLogoutHandler userLogoutHandler = new UserLogoutHandler();

    private static final HttpMethod VALID_HTTP_METHOD = HttpMethod.POST;
    private static final HttpMethod INVALID_HTTP_METHOD = HttpMethod.GET;
    private static final String VALID_REQUEST_PATH = "/users/logout";
    private static final String VALID_SESSION_ID = "sid=sessionId";
    private static final String INVALID_SESSION_ID = "sid=invalid";

    @BeforeEach
    void setUp() {
        SessionManager.saveSession("sessionId", "yulee");
    }

    private HttpRequestInfo createHttpRequest(HttpMethod method, String path, String sid) throws IOException {
        String rawRequest =
                method + " " + path + " HTTP/1.1\r\n" +
                        "Host: localhost\r\n" +
                        "Cookie: " + sid;

        InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));
        return new HttpRequestInfo(inputStream);
    }

    @Test
    @DisplayName("로그아웃 성공")
    void testHandleWithValidLoginData() throws IOException {
        HttpRequestInfo httpRequestInfo = createHttpRequest(VALID_HTTP_METHOD, VALID_REQUEST_PATH, VALID_SESSION_ID);
        HttpResponse response = userLogoutHandler.handle(httpRequestInfo);

        assertEquals(HttpStatus.FOUND, response.getStatus());
    }

    @Test
    @DisplayName("잘못된 HTTP Method인 경우")
    void testHandleWithInvalidHttpMethod() throws IOException {
        HttpRequestInfo httpRequestInfo = createHttpRequest(INVALID_HTTP_METHOD, VALID_REQUEST_PATH, VALID_SESSION_ID);

        BaseException baseException = assertThrows(BaseException.class, () -> userLogoutHandler.handle(httpRequestInfo));
        assertEquals(baseException.getMessage(), HttpErrorCode.INVALID_HTTP_METHOD.getMessage());
    }

    @Test
    @DisplayName("세션 아이디에 해당하는 유저 정보가 없는 경우")
    void testHandleWithNonExistentSessionUser() throws IOException {
        HttpRequestInfo httpRequestInfo = createHttpRequest(VALID_HTTP_METHOD, VALID_REQUEST_PATH, INVALID_SESSION_ID);

        BaseException baseException = assertThrows(BaseException.class, () -> userLogoutHandler.handle(httpRequestInfo));
        assertEquals(baseException.getMessage(), UserErrorCode.USER_NOT_FOUND_FOR_SESSION.getMessage());
    }
}
