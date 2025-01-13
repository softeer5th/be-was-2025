package webserver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.httpserver.HttpMethod;
import webserver.httpserver.HttpRequest;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    public static final String HOST = "host";
    public static final String USER_AGENT = "user-agent";
    public static final String ACCEPT = "accept";
    public static final String PARAM_1 = "param1";
    public static final String PARAM_2 = "param2";

    @Test
    @DisplayName("HTTP 메시지 파싱 검증 테스트")
    void generateHttpRequest() throws IOException {
        //given
        BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(("""
                GET /index.html HTTP/1.1\r
                Host: www.example.com\r
                User-Agent: Mozilla/5.0\r
                Accept: text/html\r
                \r
                """).getBytes()));
        //when
        HttpRequest request = new HttpRequest(bis);

        //then
        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(request.getHeader(HOST)).isEqualTo("www.example.com");
        assertThat(request.getHeader(USER_AGENT)).isEqualTo("Mozilla/5.0");
        assertThat(request.getHeader(ACCEPT)).isEqualTo("text/html");
        assertThat(request.getProtocol()).isEqualTo("HTTP/1.1");
    }

    @Test
    @DisplayName("쿼리 파라미터가 있는 HTTP 메시지 파싱 검증 테스트")
    void generateQueryParameterizedHttpRequest() throws IOException {
        //given
        BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(("""
                GET /path?param1=value1&param2=value2 HTTP/1.1\r
                Host: www.example.com\r
                User-Agent: Mozilla/5.0\r
                Accept: text/html\r
                \r
                """).getBytes()));
        //when
        HttpRequest request = new HttpRequest(bis);

        //then
        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(request.getHeader(HOST)).isEqualTo("www.example.com");
        assertThat(request.getHeader(USER_AGENT)).isEqualTo("Mozilla/5.0");
        assertThat(request.getHeader(ACCEPT)).isEqualTo("text/html");
        assertThat(request.getProtocol()).isEqualTo("HTTP/1.1");
        assertThat(request.getParameter(PARAM_1)).isEqualTo("value1");
        assertThat(request.getParameter(PARAM_2)).isEqualTo("value2");
    }

    @Test
    @DisplayName("URI가 퍼센트 인코딩된 HTTP 메시지 파싱 검증 테스트")
    void generatePercentEncodedHttpRequest() throws IOException {
        //given
        BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(("""
                GET /path?%EC%86%8C%ED%94%84%ED%8B%B0%EC%96%B4=%EB%B0%B1%EC%97%94%EB%93%9C HTTP/1.1\r
                Host: www.example.com\r
                User-Agent: Mozilla/5.0\r
                Accept: text/html\r
                \r
                """).getBytes()));
        //when
        HttpRequest request = new HttpRequest(bis);

        //then
        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(request.getHeader(HOST)).isEqualTo("www.example.com");
        assertThat(request.getHeader(USER_AGENT)).isEqualTo("Mozilla/5.0");
        assertThat(request.getHeader(ACCEPT)).isEqualTo("text/html");
        assertThat(request.getProtocol()).isEqualTo("HTTP/1.1");
        assertThat(request.getParameter("소프티어")).isEqualTo("백엔드");
    }

    @Test
    @DisplayName("개행문자를 LF로 사용하는 HTTP 메시지 검증 테스트")
    void generateLFHttpRequest() throws IOException {
        //given
        BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(("""
                GET /path?%EC%86%8C%ED%94%84%ED%8B%B0%EC%96%B4=%EB%B0%B1%EC%97%94%EB%93%9C HTTP/1.1
                Host: www.example.com
                User-Agent: Mozilla/5.0
                Accept: text/html
                
                """).getBytes()));
        //when
        HttpRequest request = new HttpRequest(bis);

        //then
        assertThat(request).isNotNull();
    }

    @Test
    @DisplayName("콜론을 포함하는 헤더 테스트")
    void customHeaderTestWithColon() throws IOException {
        //given
        BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(("""
                GET /path?%EC%86%8C%ED%94%84%ED%8B%B0%EC%96%B4=%EB%B0%B1%EC%97%94%EB%93%9C HTTP/1.1
                Host: www.example.com
                CustomHeader: asdf:293194
                User-Agent: Mozilla/5.0
                Accept: text/html
                
                """).getBytes()));
        //when
        HttpRequest request = new HttpRequest(bis);

        //then
        assertThat(request).isNotNull();
    }
}