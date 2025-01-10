package handler;

import enums.HttpMethod;
import enums.HttpStatus;
import enums.HttpVersion;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import request.HttpRequestInfo;
import response.HttpResponse;

class HomeHandlerTest {
    private final HomeHandler homeHandler = new HomeHandler();
    private final String HOME_URL = System.getenv("HOME_URL");

    @Test
    @DisplayName("/ get 요청 시 홈화면으로 리다이렉트한다.")
    void redirectToHome() {
        final HttpRequestInfo requestInfo = new HttpRequestInfo(HttpMethod.GET, "/", HttpVersion.HTTP1_1);

        final HttpResponse response = homeHandler.handle(requestInfo);

        Assertions.assertThat(response.getStatus())
                .isEqualTo(HttpStatus.SEE_OTHER);
        Assertions.assertThat(response.getHeaderValue("Location"))
                .isEqualTo(HOME_URL);
    }


}