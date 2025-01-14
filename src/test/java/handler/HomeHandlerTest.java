package handler;

import enums.HttpMethod;
import exception.ClientErrorException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import request.HttpRequestInfo;
import response.HttpResponse;

import static enums.HttpMethod.POST;
import static enums.HttpStatus.SEE_OTHER;
import static enums.HttpVersion.HTTP1_1;
import static exception.ErrorCode.INVALID_HTTP_METHOD;

class HomeHandlerTest {
    private final HomeHandler homeHandler = new HomeHandler();
    private static final String ROOT_PATH = "/";
    private final String HOME_URL = System.getenv("HOME_URL");

    @Test
    @DisplayName("/ get 요청 시 홈화면으로 리다이렉트한다.")
    void redirectToHome() {
        final HttpRequestInfo requestInfo = new HttpRequestInfo(HttpMethod.GET, ROOT_PATH, HTTP1_1);

        final HttpResponse response = homeHandler.handle(requestInfo);

        Assertions.assertThat(response.getStatus())
                .isEqualTo(SEE_OTHER);
        Assertions.assertThat(response.getHeaderValue("Location"))
                .isEqualTo(HOME_URL);
    }

    @Test
    @DisplayName("get이 아닌 다른 메소드로 / 요청 시 에러를 반환한다.")
    void redirectToHome_RequestWith_NonGetMethod() {
        final HttpRequestInfo requestInfo = new HttpRequestInfo(POST, ROOT_PATH, HTTP1_1);

        Assertions.assertThatThrownBy(() -> homeHandler.handle(requestInfo))
                .isInstanceOf(ClientErrorException.class)
                .hasMessage(INVALID_HTTP_METHOD.getMessage());
    }


}