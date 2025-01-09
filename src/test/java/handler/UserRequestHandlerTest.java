package handler;

import enums.HttpMethod;
import enums.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import request.HttpRequestInfo;
import response.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

class UserRequestHandlerTest {
    private final UserRequestHandler userRequestHandler = new UserRequestHandler();

    private static final String VALID_REQUEST_PATH = "/user/create?userId=jueun&nickname=jueun&password=jueun&email=jueun@naver.com";

    @Test
    @DisplayName("회원가입에 성공하면 201을 반환한다.")
    void handle_createUser() {
        HttpRequestInfo httpRequestInfo = new HttpRequestInfo(HttpMethod.GET, VALID_REQUEST_PATH);
        HttpResponse response = userRequestHandler.handle(httpRequestInfo);

        assertThat(response.getStatus())
                .isEqualTo(HttpStatus.CREATED);
    }

}