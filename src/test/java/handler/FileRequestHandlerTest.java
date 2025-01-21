package handler;

import db.Database;
import db.SessionManager;
import exception.BaseException;
import exception.FileErrorCode;
import http.*;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FileRequestHandlerTest {
    private FileRequestHandler handler;
    private static final HttpMethod HTTP_METHOD = HttpMethod.GET;
    private static final String VALID_FILE_PATH = "/index.html";
    private static final String INVALID_FILE_PATH = "/invalid.html";
    private static final String LOGIN_PAGE = "/login/index.html";
    private static final String RESTRICTED_PAGE = "/mypage";

    @BeforeEach
    public void setUp() {
        handler = new FileRequestHandler();
    }

    private HttpRequestInfo createTestRequest(String path) {
        return HttpRequestInfo.forTest(HTTP_METHOD, path, new HashMap<>(), new HashMap<>(), null);
    }

    private HttpRequestInfo createLoggedInRequest() {
        User testUser = new User("testId", "testUser","test1234!", "test@test.com");
        Database.addUser(testUser);

        String sid = "testSessionId";
        SessionManager.saveSession(sid, "testId");

        Map<String, Cookie> cookies = new HashMap<>();
        cookies.put("sid", new Cookie("sid", sid));

        return HttpRequestInfo.forTest(HTTP_METHOD, FileRequestHandlerTest.LOGIN_PAGE, new HashMap<>(), cookies, null);
    }

    @Test
    @DisplayName("정적파일 로드 성공")
    void testHandleWithExistingFile() {
        HttpRequestInfo request = createTestRequest(VALID_FILE_PATH);
        HttpResponse response = handler.handle(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("존재하지 않는 정적 파일 로드 실패")
    void testHandleWithFileNotFound() {
        HttpRequestInfo request = createTestRequest(INVALID_FILE_PATH);

        BaseException baseException = assertThrows(BaseException.class, () -> handler.handle(request));
        assertEquals(FileErrorCode.FILE_NOT_FOUND.getMessage(), baseException.getMessage());
    }

    @Test
    @DisplayName("로그인하지 않은 상태에서 동적 HTML 변경 없음")
    void testHandleWithDynamicHtmlOnLogin_NotLoggedIn() {
        HttpRequestInfo request = createTestRequest(LOGIN_PAGE);
        HttpResponse response = handler.handle(request);

        assertEquals(HttpStatus.OK, response.getStatus());

        String responseBody = new String(response.getBody(), StandardCharsets.UTF_8);
        assertFalse(responseBody.contains("<a class=\"user-name\" href=\"/mypage\">사용자 :"));
        assertTrue(responseBody.contains("<li class=\"header__menu__item\">"));
    }

    @Test
    @DisplayName("로그인된 상태에서 동적 HTML 변경 확인")
    void testHandleWithDynamicHtmlOnLogin_LoggedIn() {
        HttpRequestInfo request = createLoggedInRequest();
        HttpResponse response = handler.handle(request);

        assertEquals(HttpStatus.OK, response.getStatus());

        String responseBody = new String(response.getBody(), StandardCharsets.UTF_8);
        assertTrue(responseBody.contains("<a class=\"user-name\" href=\"/mypage\">사용자 : testUser</a>"));
        assertTrue(responseBody.contains("<form action=\"/users/logout\" method=\"POST\">"));
    }

    @Test
    @DisplayName("로그인하지 않은 사용자가 /mypage 접근 시 예외 발생")
    void testHandleWithRestrictedPage_Unauthorized() {
        HttpRequestInfo request = createTestRequest(RESTRICTED_PAGE);

        BaseException exception = assertThrows(BaseException.class, () -> handler.handle(request));
        assertEquals(FileErrorCode.FORBIDDEN_ACCESS.getMessage(), exception.getMessage());
    }


}
