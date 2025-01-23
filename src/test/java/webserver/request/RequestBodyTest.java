package webserver.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.enums.HttpStatusCode;
import webserver.exception.BadRequest;
import webserver.exception.HttpException;
import webserver.header.RequestHeader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RequestBodyTest {
    private RequestHeader headers;
    private InputStream body;

    @BeforeEach
    void setUp() {
        headers = mock(RequestHeader.class);
    }

    @Test
    @DisplayName("Body를 String으로 반환")
    void bodyAsString() {
        body = new ByteArrayInputStream("test body".getBytes());
        when(headers.getHeader("Content-Length")).thenReturn("9");

        RequestBody requestBody = new RequestBody(body, headers);
        Optional<String> result = requestBody.getBody(String.class);

        assertThat(result).isPresent().hasValue("test body");
    }

    @Test
    @DisplayName("Body가 없는 경우 String으로 반환")
    void bodyAsString_empty() {
        body = new ByteArrayInputStream(new byte[0]);
        when(headers.getHeader("Content-Length")).thenReturn("0");

        RequestBody requestBody = new RequestBody(body, headers);
        Optional<String> result = requestBody.getBody(String.class);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("application/x-www-form-urlencoded 형식의 Body를 Map으로 반환")
    void bodyAsMap_urlEncoded() {
        body = new ByteArrayInputStream("key1=value1&key2=value2".getBytes());
        when(headers.getHeader("Content-Length")).thenReturn("23");
        when(headers.getHeader("Content-Type")).thenReturn("application/x-www-form-urlencoded");

        RequestBody requestBody = new RequestBody(body, headers);
        Optional<Map> result = requestBody.getBody(Map.class);

        assertThat(result).isPresent().hasValue(Map.of("key1", "value1", "key2", "value2"));
    }

    @Test
    @DisplayName("application/x-www-form-urlencoded 형식의 Body가 없는 경우 Map으로 반환")
    void bodyAsMap_urlEncoded_empty() {
        body = new ByteArrayInputStream(new byte[0]);
        when(headers.getHeader("Content-Length")).thenReturn("0");
        when(headers.getHeader("Content-Type")).thenReturn("application/x-www-form-urlencoded");

        RequestBody requestBody = new RequestBody(body, headers);
        Optional<Map> result = requestBody.getBody(Map.class);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Content-Length 헤더가 없는 경우 예외 발생")
    void missingContentLengthHeader_throwsException() {
        body = new ByteArrayInputStream("test body".getBytes());

        RequestBody requestBody = new RequestBody(body, headers);

        assertThatThrownBy(() -> requestBody.getBody(String.class))
                .isInstanceOf(HttpException.class)
                .matches(e -> ((HttpException) e).getStatusCode() == HttpStatusCode.LENGTH_REQUIRED.statusCode);
    }


    @Test
    @DisplayName("Body가 비어있을 때 Content-Length가 0이 아닌 경우 예외 발생")
    void bodyAsString_nonZeroContentLength_throwsException() {
        body = new ByteArrayInputStream(new byte[0]);
        when(headers.getHeader("Content-Length")).thenReturn("10");

        RequestBody requestBody = new RequestBody(body, headers);

        assertThatThrownBy(() -> requestBody.getBody(String.class))
                .isInstanceOf(BadRequest.class);
    }

    @Test
    @DisplayName("Body가 Content-Length보다 작은 경우 예외 발생")
    void bodyAsString_lessContentLength_throwsException() {
        body = new ByteArrayInputStream("test body".getBytes());
        when(headers.getHeader("Content-Length")).thenReturn("100");

        RequestBody requestBody = new RequestBody(body, headers);

        assertThatThrownBy(() -> requestBody.getBody(String.class))
                .isInstanceOf(BadRequest.class);
    }
}
