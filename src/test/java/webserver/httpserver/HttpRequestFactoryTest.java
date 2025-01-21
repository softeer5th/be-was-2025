package webserver.httpserver;

import exception.MethodNotAllowedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.httpserver.header.Cookie;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

class HttpRequestFactoryTest {
    public static final String HOST = "host";
    public static final String USER_AGENT = "user-agent";
    public static final String ACCEPT = "accept";
    public static final String PARAM_1 = "param1";
    public static final String PARAM_2 = "param2";
    public static final String CONTENT_TYPE = "content-type";
    public static final String CONTENT_LENGTH = "content-length";
    private HttpRequestFactory requestFactory;

    @BeforeEach
    void setUp() {
        requestFactory = new HttpRequestFactory();
    }

    @Test
    @DisplayName("쿼리 파라미터가 있는 HTTP 메시지 파싱 검증 테스트")
    void queryParameterizedHttpRequest() throws IOException {
        //given
        BufferedInputStream bis = getBufferedInputStream("""
                GET /path?param1=value1&param2=value2 HTTP/1.1\r
                Host: www.example.com\r
                User-Agent: Mozilla/5.0\r
                Accept: text/html\r
                \r
                """);
        //when
        HttpRequest request = requestFactory.getHttpRequest(bis);

        //then
        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(request.getHeader(HOST).orElseThrow(NoSuchElementException::new)).isEqualTo("www.example.com");
        assertThat(request.getHeader(USER_AGENT).orElseThrow(NoSuchElementException::new)).isEqualTo("Mozilla/5.0");
        assertThat(request.getHeader(ACCEPT).orElseThrow(NoSuchElementException::new)).isEqualTo("text/html");
        assertThat(request.getProtocol()).isEqualTo("HTTP/1.1");
        assertThat(request.getParameter(PARAM_1)).isEqualTo("value1");
        assertThat(request.getParameter(PARAM_2)).isEqualTo("value2");
    }


    @Test
    @DisplayName("x-www-form-urlencoded 바디 파싱 테스트")
    void x_www_form_urlencodedTest() throws IOException {
        // given
        String requestBody = "userId=joonho&password=1234";
        BufferedInputStream bis = getBufferedInputStream("POST /hello HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Content-Type: application/x-www-form-urlencoded\r\n" +
                "Content-Length: " + requestBody.getBytes().length + "\r\n" +
                "\r\n" +
                requestBody);

        // when
        HttpRequest httpRequest = requestFactory.getHttpRequest(bis);

        // then
        assertThat(httpRequest.getMethod()).isEqualTo(HttpMethod.POST);
        assertThat(httpRequest.getUri()).isEqualTo("/hello");
        assertThat(httpRequest.getProtocol()).isEqualTo("HTTP/1.1");
        assertThat(httpRequest.getHeader(CONTENT_TYPE).orElseThrow(NoSuchElementException::new)).isEqualTo("application/x-www-form-urlencoded");
        assertThat(httpRequest.getHeader(CONTENT_LENGTH).orElseThrow(NoSuchElementException::new)).isEqualTo(String.valueOf(requestBody.getBytes().length));
        assertThat(httpRequest.getParameter("userId")).isEqualTo("joonho");
        assertThat(httpRequest.getParameter("password")).isEqualTo("1234");
    }

    @Test
    @DisplayName("헤더가 없는 요청 파싱 테스트")
    void requestWithoutHeadersTest() throws IOException {
        // given
        BufferedInputStream bis = getBufferedInputStream("GET / HTTP/1.1\r\n" +
                "\r\n");

        // when
        HttpRequest httpRequest = requestFactory.getHttpRequest(bis);

        // then
        assertThat(httpRequest.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(httpRequest.getUri()).isEqualTo("/");
        assertThat(httpRequest.getProtocol()).isEqualTo("HTTP/1.1");
    }

    @Test
    @DisplayName("잘못된 요청 - 빈 첫줄 파싱 테스트")
    void requestWithEmptyFirstLine() throws IOException {
        // given
        BufferedInputStream bis = getBufferedInputStream("GET / HTTP/1.1\r\n" +
                "\r\n");

        // when
        HttpRequest httpRequest = requestFactory.getHttpRequest(bis);

        // then
        assertThat(httpRequest.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(httpRequest.getUri()).isEqualTo("/");
        assertThat(httpRequest.getProtocol()).isEqualTo("HTTP/1.1");
    }

    @Test
    @DisplayName("올바르지 않은 요청 라인(공백이 2개 미만 또는 초과)일 때 예외 발생")
    void requestWithTooLittleSpace() {
        // given
        BufferedInputStream bis = getBufferedInputStream("GET /hello\r\n" +
                "\r\n");

        // when & then
        assertThatThrownBy(() -> requestFactory.getHttpRequest(bis))
                .isInstanceOf(MethodNotAllowedException.class);
    }

    @Test
    @DisplayName("쿼리 파라미터 형식이 잘못되었을 때 예외 발생")
    void requestWithWrongQueryParameter() {
        // given
        BufferedInputStream bis = getBufferedInputStream("GET /test?param1=value1=value2 HTTP/1.1\r\n" +
                "\r\n");

        // when & then
        assertThatThrownBy(() -> requestFactory.getHttpRequest(bis))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Cookie 객체 생성 테스트")
    void getHttpRequest_withCookieHeader() throws IOException {
        // given
        BufferedInputStream bis = getBufferedInputStream("GET /test HTTP/1.1\r\n" +
                "Cookie: userId=joonho; hello=world\r\n" +
                "\r\n");

        // when
        HttpRequest httpRequest = requestFactory.getHttpRequest(bis);

        // then
        Cookie cookie = httpRequest.getCookie();
        assertThat(cookie).isNotNull();
        assertThat(cookie.getCookie("userId")).isEqualTo("joonho");
        assertThat(cookie.getCookie("hello")).isEqualTo("world");
    }

    @Test
    @DisplayName("URI가 퍼센트 인코딩된 HTTP 메시지 파싱 검증 테스트")
    void generatePercentEncodedHttpRequest() throws IOException {
        //given
        BufferedInputStream bis = getBufferedInputStream("""
                GET /path?%EC%86%8C%ED%94%84%ED%8B%B0%EC%96%B4=%EB%B0%B1%EC%97%94%EB%93%9C HTTP/1.1\r
                Host: www.example.com\r
                User-Agent: Mozilla/5.0\r
                Accept: text/html\r
                \r
                """);
        //when
        HttpRequest request = requestFactory.getHttpRequest(bis);

        //then
        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(request.getHeader(HOST).orElseThrow(NoSuchElementException::new)).isEqualTo("www.example.com");
        assertThat(request.getHeader(USER_AGENT).orElseThrow(NoSuchElementException::new)).isEqualTo("Mozilla/5.0");
        assertThat(request.getHeader(ACCEPT).orElseThrow(NoSuchElementException::new)).isEqualTo("text/html");
        assertThat(request.getProtocol()).isEqualTo("HTTP/1.1");
        assertThat(request.getParameter("소프티어")).isEqualTo("백엔드");
    }

    private static BufferedInputStream getBufferedInputStream(String requestBody) {
        return new BufferedInputStream(
                new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8))
        );
    }
}
