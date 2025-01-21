package handler;

import static org.junit.jupiter.api.Assertions.*;

import db.Database;
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

import java.util.HashMap;

class UserRegisterHandlerTest {

    private final UserRegisterHandler userRegisterHandler = new UserRegisterHandler();

    private static final HttpMethod VALID_HTTP_METHOD = HttpMethod.POST;
    private static final HttpMethod INVALID_HTTP_METHOD = HttpMethod.GET;
    private static final String VALID_REQUEST_PATH = "/users/register";

    @BeforeEach
    void setUp() {
        Database.clear();
    }


    private HttpRequestInfo createTestRequest(String body) {
        return HttpRequestInfo.forTest(VALID_HTTP_METHOD, VALID_REQUEST_PATH, new HashMap<>(), new HashMap<>(), body);
    }

    @Test
    @DisplayName("회원가입 성공")
    void testHandleWithValidUserData() {
        HttpRequestInfo request = createTestRequest("userId=testId&nickname=testUser&password=test1234!&email=test@test.com");
        HttpResponse response = userRegisterHandler.handle(request);

        assertEquals(HttpStatus.FOUND, response.getStatus());
    }

    @Test
    @DisplayName("잘못된 HTTP Method인 경우")
    void testHandleWithInvalidHttpMethod() {
        HttpRequestInfo request = HttpRequestInfo.forTest(INVALID_HTTP_METHOD, VALID_REQUEST_PATH, new HashMap<>(), new HashMap<>(), "userId=testId&nickname=testUser&password=test1234!&email=test@test.com");

        BaseException exception = assertThrows(BaseException.class, () -> userRegisterHandler.handle(request));
        assertEquals(HttpErrorCode.INVALID_HTTP_METHOD.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("잘못된 아이디 형식")
    void testHandleWithMissingUserId() {
        HttpRequestInfo request = createTestRequest("userId=아이디&nickname=testUser&password=test1234!&email=test@test.com");

        BaseException exception = assertThrows(BaseException.class, () -> userRegisterHandler.handle(request));
        assertEquals(UserErrorCode.INVALID_USER_ID.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("잘못된 닉네임 형식")
    void testHandleWithMissingUserNickname() {
        HttpRequestInfo request = createTestRequest("userId=testId&nickname=!testUser&password=test1234!&email=test@test.com");

        BaseException exception = assertThrows(BaseException.class, () -> userRegisterHandler.handle(request));
        assertEquals(UserErrorCode.INVALID_NICKNAME.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("잘못된 이메일 형식")
    void testHandleWithInvalidEmail() {
        HttpRequestInfo request = createTestRequest("userId=testId&nickname=testUser&password=test1234!&email=testtest.com");

        BaseException exception = assertThrows(BaseException.class, () -> userRegisterHandler.handle(request));
        assertEquals(UserErrorCode.INVALID_EMAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("비밀번호가 약한 경우")
    void testHandleWithWeakPassword() {
        HttpRequestInfo request = createTestRequest("userId=testId&nickname=testUser&password=test1234&email=test@test.com");

        BaseException exception = assertThrows(BaseException.class, () -> userRegisterHandler.handle(request));
        assertEquals(UserErrorCode.INVALID_PASSWORD.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("중복된 userId로 회원가입 시도")
    void testHandleWithDuplicateUserId() {
        // 첫 번째 회원가입
        HttpRequestInfo firstRequest = createTestRequest("userId=testId&nickname=testUser&password=test1234!&email=test@test.com");
        userRegisterHandler.handle(firstRequest);

        // 동일한 userId로 두 번째 회원가입 시도
        HttpRequestInfo secondRequest = createTestRequest("userId=testId&nickname=testUser&password=test1234!&email=test@test.com");

        BaseException exception = assertThrows(BaseException.class, () -> userRegisterHandler.handle(secondRequest));
        assertEquals(UserErrorCode.DUPLICATE_USER_ID.getMessage(), exception.getMessage());
    }
}
