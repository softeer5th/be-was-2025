package webserver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.httpserver.HttpMethod;
import webserver.httpserver.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {
    @Test
    @DisplayName("HTTP 메시지 파싱 검증 테스트")
    void generateHttpRequest() throws IOException {
        //given
        BufferedReader reader = new BufferedReader(new StringReader("GET /index.html HTTP/1.1\r\n" + "Host: www.example.com\r\n" + "User-Agent: Mozilla/5.0\r\n" + "Accept: text/html\r\n" + "\r\n"));
        //when
        HttpRequest request = new HttpRequest(reader);

        //then
        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(request.getHeader("Host")).isEqualTo("www.example.com");
        assertThat(request.getHeader("User-Agent")).isEqualTo("Mozilla/5.0");
        assertThat(request.getHeader("Accept")).isEqualTo("text/html");
        assertThat(request.getProtocol()).isEqualTo("HTTP/1.1");
    }

    @Test
    @DisplayName("쿼리 파라미터가 있는 HTTP 메시지 파싱 검증 테스트")
    void generateQueryParameterizedHttpRequest() throws IOException {
        //given
        BufferedReader reader = new BufferedReader(new StringReader("GET /path?param1=value1&param2=value2 " + "HTTP/1.1\r\n" + "Host: www.example.com\r\n" + "User-Agent: Mozilla/5.0\r\n" + "Accept: text/html\r\n" + "\r\n"));
        //when
        HttpRequest request = new HttpRequest(reader);

        //then
        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(request.getHeader("Host")).isEqualTo("www.example.com");
        assertThat(request.getHeader("User-Agent")).isEqualTo("Mozilla/5.0");
        assertThat(request.getHeader("Accept")).isEqualTo("text/html");
        assertThat(request.getProtocol()).isEqualTo("HTTP/1.1");
        assertThat(request.getParameter("param1")).isEqualTo("value1");
        assertThat(request.getParameter("param2")).isEqualTo("value2");
    }

    @Test
    @DisplayName("URI가 퍼센트 인코딩된 HTTP 메시지 파싱 검증 테스트")
    void generatePercentEncodedHttpRequest() throws IOException {
        //given
        BufferedReader reader = new BufferedReader(new StringReader("GET /path" + "?%EC%86%8C%ED%94%84%ED%8B%B0%EC%96%B4=%EB%B0%B1%EC%97%94%EB%93%9C " + "HTTP/1.1\r\n" + "Host: www.example.com\r\n" + "User-Agent: Mozilla/5.0\r\n" + "Accept: text/html\r\n" + "\r\n"));
        //when
        HttpRequest request = new HttpRequest(reader);

        //then
        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(request.getHeader("Host")).isEqualTo("www.example.com");
        assertThat(request.getHeader("User-Agent")).isEqualTo("Mozilla/5.0");
        assertThat(request.getHeader("Accept")).isEqualTo("text/html");
        assertThat(request.getProtocol()).isEqualTo("HTTP/1.1");
        assertThat(request.getParameter("소프티어")).isEqualTo("백엔드");
    }

    @Test
    @DisplayName("개행문자를 LF로 사용하는 HTTP 메시지 검증 테스트")
    void generateLFHttpRequest() throws IOException {
        //given
        BufferedReader reader = new BufferedReader(new StringReader("GET /path" + "?%EC%86%8C%ED%94%84%ED%8B%B0%EC%96%B4=%EB%B0%B1%EC%97%94%EB%93%9C " + "HTTP/1.1\n" + "Host: www.example.com\n" + "User-Agent: Mozilla/5.0\n" + "Accept: text/html\n" + "\n"));
        //when
        HttpRequest request = new HttpRequest(reader);

        //then
        assertThat(request).isNotNull();
    }
}