package util;

import enums.HttpMethod;
import exception.ClientErrorException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import request.HttpRequestInfo;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static exception.ErrorCode.*;

class HttpRequestParserTest {

    @Test
    @DisplayName("올바른 http 요청을 파싱한다.")
    void parse_validHttpRequest() throws IOException {
        String httpRequest = "POST /test HTTP/1.1\r\n" +
                "Accept: application/json\r\n" +
                "Accept-Encoding: gzip, deflate\r\n" +
                "Connection: keep-alive\r\n" +
                "Content-Length: 4\r\n" +
                "Content-Type: application/json\r\n" +
                "Host: google.com\r\n" +
                "User-Agent: HTTPie/0.9.3\r\n" +
                "\r\n" +
                "gigi\r\n";

        byte[] byteArray = httpRequest.getBytes(StandardCharsets.UTF_8);
        InputStream inputStream = new ByteArrayInputStream(byteArray);

        final HttpRequestInfo requestInfo = HttpRequestParser.parse(inputStream);

        Assertions.assertThat(requestInfo.getMethod())
                .isEqualTo(HttpMethod.POST);
        Assertions.assertThat(requestInfo.getPath())
                .isEqualTo("/test");
        Assertions.assertThat(requestInfo.getBody())
                .isEqualTo("gigi");
    }

    @Test
    @DisplayName("올바른 http 요청을 파싱한다. 첫줄에 crlf가 있을 경우는 무시한다.")
    void parse_validHttpRequest2() throws IOException {
        String httpRequest =
                "\r\n"+
                "POST /test HTTP/1.1\r\n" +
                "Accept: application/json\r\n" +
                "Accept-Encoding: gzip, deflate\r\n" +
                "Connection: keep-alive\r\n" +
                "Content-Length: 4\r\n" +
                "Content-Type: application/json\r\n" +
                "Host: google.com\r\n" +
                "User-Agent: HTTPie/0.9.3\r\n" +
                "\r\n" +
                "gigi\r\n";

        byte[] byteArray = httpRequest.getBytes(StandardCharsets.UTF_8);
        InputStream inputStream = new ByteArrayInputStream(byteArray);

        final HttpRequestInfo requestInfo = HttpRequestParser.parse(inputStream);

        Assertions.assertThat(requestInfo.getMethod())
                .isEqualTo(HttpMethod.POST);
        Assertions.assertThat(requestInfo.getPath())
                .isEqualTo("/test");
        Assertions.assertThat(requestInfo.getBody())
                .isEqualTo("gigi");
    }

    @Test
    @DisplayName("http method는 대소문자를 구분한다")
    void parse_httpMethod() {
        String httpRequest = "post /test HTTP/1.1\r\n" +
                "Accept: application/json\r\n" +
                "Accept-Encoding: gzip, deflate\r\n" +
                "Connection: keep-alive\r\n" +
                "Content-Length: 4\r\n" +
                "Content-Type: application/json\r\n" +
                "Host: google.com\r\n" +
                "User-Agent: HTTPie/0.9.3\r\n" +
                "\r\n" +
                "gigi\r\n";


        byte[] byteArray = httpRequest.getBytes(StandardCharsets.UTF_8);
        InputStream inputStream = new ByteArrayInputStream(byteArray);

        Assertions.assertThatThrownBy(() -> HttpRequestParser.parse(inputStream))
                .isInstanceOf(ClientErrorException.class)
                .hasMessage(INVALID_HTTP_METHOD.getMessage());
    }

    @Test
    @DisplayName("Request Line의 규격에 맞지 않은 http 요청이 오면 에러가 발생한다")
    void parse_invalidHttpRequestLine() {
        String httpRequest = "POST /test HTTP/1.1 invalid\r\n" +
                "Accept: application/json\r\n" +
                "Accept-Encoding: gzip, deflate\r\n" +
                "Connection: keep-alive\r\n" +
                "Content-Length: 4\r\n" +
                "Content-Type: application/json\r\n" +
                "Host: google.com\r\n" +
                "User-Agent: HTTPie/0.9.3\r\n" +
                "\r\n" +
                "gigi\r\n";


        byte[] byteArray = httpRequest.getBytes(StandardCharsets.UTF_8);
        InputStream inputStream = new ByteArrayInputStream(byteArray);

        Assertions.assertThatThrownBy(() -> HttpRequestParser.parse(inputStream))
                .isInstanceOf(ClientErrorException.class)
                .hasMessage(INVALID_HTTP_REQUEST.getMessage());
    }

    @Test
    @DisplayName("지원하지 않는 http 버전으로 요청이 올 경우 에러가 발생한다.")
    void parse_invalidHttpVersion() {
        String httpRequest = "POST /test HTTP/1.2\r\n" +
                "Accept: application/json\r\n" +
                "Accept-Encoding: gzip, deflate\r\n" +
                "Connection: keep-alive\r\n" +
                "Content-Length: 4\r\n" +
                "Content-Type: application/json\r\n" +
                "Host: google.com\r\n" +
                "User-Agent: HTTPie/0.9.3\r\n" +
                "\r\n" +
                "gigi\r\n";


        byte[] byteArray = httpRequest.getBytes(StandardCharsets.UTF_8);
        InputStream inputStream = new ByteArrayInputStream(byteArray);

        Assertions.assertThatThrownBy(() -> HttpRequestParser.parse(inputStream))
                .isInstanceOf(ClientErrorException.class)
                .hasMessage(UNSUPPORTED_HTTP_VERSION.getMessage());
    }
}