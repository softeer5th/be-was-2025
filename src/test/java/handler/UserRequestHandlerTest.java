package handler;

import db.UserDatabase;
import enums.HttpHeader;
import enums.HttpMethod;
import enums.HttpStatus;
import enums.HttpVersion;
import exception.ClientErrorException;
import exception.ErrorCode;
import exception.LoginException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import request.HttpRequestInfo;
import response.HttpResponse;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserRequestHandlerTest {
    private final UserRequestHandler userRequestHandler = new UserRequestHandler();

    private static final String VALID_REQUEST_PATH = "/user/create?userId=new&nickname=jueun&password=jueun1025^^&email=jueun@naver.com";
    private static final String REDIRECT_URL = "http://localhost:8080/index.html";

    @Test
    @DisplayName("회원가입에 성공하면 201을 반환한다.")
    void handle_createUser() {
        HttpRequestInfo httpRequestInfo = new HttpRequestInfo(HttpMethod.POST, "/user/create", HttpVersion.HTTP1_1, new HashMap<>(), "userId=jueun2&nickname=jueun&password=jueun1025^^&email=jueun@naver.com");
        HttpResponse response = userRequestHandler.handle(httpRequestInfo);

        assertThat(response.getStatus())
                .isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaderValue(HttpHeader.LOCATION.getName()))
                .isEqualTo(REDIRECT_URL);
    }

    @Test
    @DisplayName("회원가입 항목 필드에 누락이 있을 경우 에러가 발생한다.")
    void handle_createUser_missing_field() {
        HttpRequestInfo httpRequestInfo = new HttpRequestInfo(HttpMethod.POST, "/user/create", HttpVersion.HTTP1_1, new HashMap<>(), "userId=jueun2&nickname=jueun&password=jueun");
        assertThatThrownBy(() -> userRequestHandler.handle(httpRequestInfo))
                .isInstanceOf(ClientErrorException.class)
                .hasMessage(ErrorCode.MISSING_FIELD.getMessage());
    }

    @Test
    @DisplayName("GET으로 회원가입 요청 시 에러가 발생한다.")
    void handle_createUser_GET() {
        HttpRequestInfo httpRequestInfo = new HttpRequestInfo(HttpMethod.GET, VALID_REQUEST_PATH, HttpVersion.HTTP1_1, new HashMap<>(), null);
        assertThatThrownBy(() -> userRequestHandler.handle(httpRequestInfo))
                .isInstanceOf(ClientErrorException.class)
                .hasMessage(ErrorCode.REQUEST_NOT_ALLOWED.getMessage());

    }

    private final String VALID_LOGIN_BODY = "userId=test&password=test";
    private final String INVALID_LOGIN_BODY = "userId=invalid&password=invalid";
    private final String LOGIN_PATH = "/user/login";

    @Test
    @DisplayName("로그인에 성공한다.")
    void handle_loginUser() {
        HttpRequestInfo httpRequestInfo = new HttpRequestInfo(HttpMethod.POST, LOGIN_PATH, HttpVersion.HTTP1_1, new HashMap<>(), VALID_LOGIN_BODY);

        final HttpResponse response = userRequestHandler.handle(httpRequestInfo);

        assertThat(response.getStatus())
                .isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaderValue(HttpHeader.LOCATION.getName()))
                .isEqualTo(REDIRECT_URL);
        assertThat(response.getHeaderValue(HttpHeader.SET_COOKIE.getName()))
                .isNotEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 로그인하면 에러가 발생한다.")
    void handle_loginUser_invalid_user() {
        HttpRequestInfo httpRequestInfo = new HttpRequestInfo(HttpMethod.POST, LOGIN_PATH, HttpVersion.HTTP1_1, new HashMap<>(), INVALID_LOGIN_BODY);

        assertThatThrownBy(()-> userRequestHandler.handle(httpRequestInfo))
                .isInstanceOf(LoginException.class)
                .hasMessage(ErrorCode.NO_SUCH_USER_ID.getMessage());


    }

    @Test
    @DisplayName("로그아웃 한다.")
    void handle_logout() {
        HttpRequestInfo login = new HttpRequestInfo(HttpMethod.POST, LOGIN_PATH, HttpVersion.HTTP1_1, new HashMap<>(), VALID_LOGIN_BODY);


        final HttpResponse loginResponse = userRequestHandler.handle(login);

        final String setCookie = loginResponse.getHeaderValue(HttpHeader.SET_COOKIE.getName());
        // SID=PpbZz; Path=/
        final String sessionId = setCookie.split(";")[0].substring("SID=".length());
        HttpRequestInfo logout = new HttpRequestInfo(HttpMethod.GET, "/user/logout", HttpVersion.HTTP1_1, Map.of(HttpHeader.SET_COOKIE.getName(), sessionId), "");
        final HttpResponse response = userRequestHandler.handle(logout);

        assertThat(response.getHeaderValue(HttpHeader.SET_COOKIE.getName()))
                .isEqualTo("SID=; Expires=Thu, 01 Jan 1970 00:00:00 GMT; Path=/");


    }
}