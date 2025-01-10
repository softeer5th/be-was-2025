package handler;

import enums.HttpMethod;
import enums.HttpStatus;
import enums.HttpVersion;
import exception.ClientErrorException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import request.HttpRequestInfo;
import response.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static exception.ErrorCode.FILE_NOT_FOUND;

class StaticFileHandlerTest {
    private final StaticFileHandler staticFileHandler = new StaticFileHandler();

    @Test
    @DisplayName("서버가 제공하는 유효한 정적 파일이면 200 상태코드와 함께 body에 파일의 내용을 전송한다..")
    void handle() throws IOException {
        HttpRequestInfo request = new HttpRequestInfo(HttpMethod.GET, "/static/test.html", HttpVersion.HTTP1);
        byte[] expected = Files.readAllBytes(new File("src/test/resources/static/test.html").toPath());

        HttpResponse response = staticFileHandler.handle(request);

        Assertions.assertThat(response.getStatus())
                .isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody())
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("서버가 제공하지 않는 정적파일이면 예외가 발생한다.")
    void handle_invalidFileRequest() {
        HttpRequestInfo request = new HttpRequestInfo(HttpMethod.GET, "/invalid.html",HttpVersion.HTTP1);

        Assertions.assertThatThrownBy(() -> staticFileHandler.handle(request))
                .isInstanceOf(ClientErrorException.class)
                .hasMessage(FILE_NOT_FOUND.getMessage());

    }

}