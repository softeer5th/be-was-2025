package handler;

import db.Database;
import db.SessionManager;
import exception.BaseException;
import exception.HttpErrorCode;
import exception.UserErrorCode;
import http.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserLogoutHandlerTest {
    private final UserLogoutHandler userLogoutHandler = new UserLogoutHandler();

    private static final HttpMethod VALID_HTTP_METHOD = HttpMethod.POST;
    private static final HttpMethod INVALID_HTTP_METHOD = HttpMethod.GET;
    private static final String VALID_REQUEST_PATH = "/users/logout";
    private static final String VALID_SESSION_ID = "validSessionId";
    private static final String EXPIRED_SESSION_ID = "expiredSessionId";
    private static final String INVALID_SESSION_ID = "invalidSessionId";

    @BeforeEach
    void setUp() {
        Database.clear();
        SessionManager.clear();

        // 유효한 세션 저장
        SessionManager.saveSession(VALID_SESSION_ID, "testId");

        // 만료된 세션 저장 (사용자를 찾을 수 없는 상태)
        SessionManager.saveSession(EXPIRED_SESSION_ID, "testId");
        SessionManager.setSessionExpire(EXPIRED_SESSION_ID, -1);
    }

    private HttpRequestInfo createTestRequestWithSession(String sessionId) {
        Cookie cookie = new Cookie("sid", sessionId);
        Map<String, Cookie> cookies = new HashMap<>();
        cookies.put("sid", cookie);
        return HttpRequestInfo.forTest(VALID_HTTP_METHOD, VALID_REQUEST_PATH, new HashMap<>(), cookies, null);
    }


    @Test
    @DisplayName("로그아웃 성공")
    void testHandleWithValidLoginData() {
        HttpRequestInfo request = createTestRequestWithSession(VALID_SESSION_ID);
        HttpResponse response = userLogoutHandler.handle(request);

        assertEquals(HttpStatus.FOUND, response.getStatus());
    }

    @Test
    @DisplayName("잘못된 HTTP Method인 경우")
    void testHandleWithInvalidHttpMethod() {
        HttpRequestInfo httpRequestInfo = HttpRequestInfo.forTest(INVALID_HTTP_METHOD, VALID_REQUEST_PATH, new HashMap<>(), new HashMap<>(), null);

        BaseException baseException = assertThrows(BaseException.class, () -> userLogoutHandler.handle(httpRequestInfo));
        assertEquals(baseException.getMessage(), HttpErrorCode.INVALID_HTTP_METHOD.getMessage());
    }

    @Test
    @DisplayName("세션매니저에 세션 ID가 있지만 만료된 경우")
    void testHandleWithExpiredSession() {
        HttpRequestInfo request = createTestRequestWithSession(EXPIRED_SESSION_ID);

        BaseException exception = assertThrows(BaseException.class, () -> userLogoutHandler.handle(request));
        assertEquals(UserErrorCode.USER_NOT_FOUND_FOR_SESSION.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("세션 매니저에 세션 ID가 있지만 해당 세션을 찾을 수 없는 경우")
    void testHandleWithInvalidSession() {
        HttpRequestInfo request = createTestRequestWithSession(INVALID_SESSION_ID);

        BaseException exception = assertThrows(BaseException.class, () -> userLogoutHandler.handle(request));
        assertEquals(UserErrorCode.USER_NOT_FOUND_FOR_SESSION.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("요청 쿠키 헤더에 세션 ID 자체가 없는 경우")
    void testHandleWithNoSessionCookie() {
        HttpRequestInfo request = HttpRequestInfo.forTest(VALID_HTTP_METHOD, VALID_REQUEST_PATH, new HashMap<>(), new HashMap<>(), null);

        BaseException exception = assertThrows(BaseException.class, () -> userLogoutHandler.handle(request));
        assertEquals(UserErrorCode.MISSING_SESSION.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("요청 쿠키 헤더에 세션 ID는 속성은 있지만 값은 없는 경우")
    void testHandleWithEmptySessionId() {
        Cookie cookie = new Cookie("sid", "");
        Map<String, Cookie> cookies = new HashMap<>();
        cookies.put("sid", cookie);
        HttpRequestInfo request = HttpRequestInfo.forTest(VALID_HTTP_METHOD, VALID_REQUEST_PATH, new HashMap<>(), cookies, null);

        BaseException exception = assertThrows(BaseException.class, () -> userLogoutHandler.handle(request));
        assertEquals(UserErrorCode.INVALID_SESSION.getMessage(), exception.getMessage());
    }

}
