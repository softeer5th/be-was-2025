package handler;

import enums.HttpMethod;
import enums.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import request.RequestInfo;
import response.HttpResponse;

class StaticFileHandlerTest {
    private final StaticFileHandler staticFileHandler = new StaticFileHandler();

    @Test
    @DisplayName("서버가 제공하는 유효한 정적 파일이면 200 요청과 함께 body에 파일의 내용을 보낸다.")
    void handle() {
        RequestInfo request = new RequestInfo(HttpMethod.GET, "/test.html");
        byte[] expected = "<h1>test</h1>".getBytes();

        HttpResponse response = staticFileHandler.handle(request);

        Assertions.assertThat(response.getStatus())
                .isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody())
                .isEqualTo(expected);
    }

}