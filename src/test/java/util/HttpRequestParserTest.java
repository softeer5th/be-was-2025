package util;

import enums.HttpMethod;
import exception.ClientErrorException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import request.HttpRequestInfo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static exception.ErrorCode.INVALID_HTTP_REQUEST;

class HttpRequestParserTest {
    @Test
    @DisplayName("올바른 http 요청을 파싱한다.")
    void parse_validHttpRequest() throws IOException {
        InputStream inputStream = new FileInputStream("src/test/java/fixtureUtil/HttpRequestSample");

        final HttpRequestInfo requestInfo = HttpRequestParser.parse(inputStream);

        Assertions.assertThat(requestInfo.getMethod())
                .isEqualTo(HttpMethod.POST);
        Assertions.assertThat(requestInfo.getPath())
                .isEqualTo("/test");
    }

    @Test
    @DisplayName("http method는 대소문자를 구분하지 않는다.")
    void parse_httpMethod() throws IOException {
        InputStream inputStream = new FileInputStream("src/test/java/fixtureUtil/HttpRequestSample_CaseInsensitive");

        final HttpRequestInfo requestInfo = HttpRequestParser.parse(inputStream);

        Assertions.assertThat(requestInfo.getMethod())
                .isEqualTo(HttpMethod.POST);
        Assertions.assertThat(requestInfo.getPath())
                .isEqualTo("/test");
    }

    @Test
    @DisplayName("올바르지 않은 http 요청이 오면 에러가 발생한다.")
    void parse_invalidHttpRequest() throws IOException {
        InputStream inputStream = new FileInputStream("src/test/java/fixtureUtil/invalid_HttpRequestSample");

        Assertions.assertThatThrownBy(() -> HttpRequestParser.parse(inputStream))
                .isInstanceOf(ClientErrorException.class)
                .hasMessage(INVALID_HTTP_REQUEST.getMessage());
    }

    @Test
    @DisplayName("Request Line의 규격에 맞지 않은 http 요청이 오면 에러가 발생한다")
    void parse_invalidHttpRequestLine() throws IOException{
        InputStream inputStream = new FileInputStream("src/test/java/fixtureUtil/invalid_HttpRequestSample_in_RequestLine");

        Assertions.assertThatThrownBy(() -> HttpRequestParser.parse(inputStream))
                .isInstanceOf(ClientErrorException.class)
                .hasMessage(INVALID_HTTP_REQUEST.getMessage());
    }
}