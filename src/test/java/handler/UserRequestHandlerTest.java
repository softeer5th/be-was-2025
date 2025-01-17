package handler;

import enums.HttpMethod;
import enums.HttpStatus;
import enums.HttpVersion;
import exception.ClientErrorException;
import exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import request.HttpRequestInfo;
import response.HttpResponse;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserRequestHandlerTest {
    private final UserRequestHandler userRequestHandler = new UserRequestHandler();

    private static final String VALID_REQUEST_PATH = "/user/create?userId=jueun&nickname=jueun&password=jueun&email=jueun@naver.com";

    @Test
    @DisplayName("회원가입에 성공하면 201을 반환한다.")
    void handle_createUser() {
        HttpRequestInfo httpRequestInfo = new HttpRequestInfo(HttpMethod.POST, "/user/create", HttpVersion.HTTP1_1, new HashMap<>(), "userId=jueun&nickname=jueun&password=jueun&email=jueun@naver.com");
        HttpResponse response = userRequestHandler.handle(httpRequestInfo);

        assertThat(response.getStatus())
                .isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaderValue("Location"))
                .isEqualTo("http://localhost:8080/index.html");
    }

    @Test
    @DisplayName("GET으로 회원가입 요청 시 에러가 발생한다.")
    void handle_createUser_GET(){
        HttpRequestInfo httpRequestInfo = new HttpRequestInfo(HttpMethod.GET, VALID_REQUEST_PATH, HttpVersion.HTTP1_1, new HashMap<>(), null);
        assertThatThrownBy(()-> userRequestHandler.handle(httpRequestInfo))
                .isInstanceOf(ClientErrorException.class)
                .hasMessage(ErrorCode.METHOD_NOT_ALLOWED.getMessage());

    }
}