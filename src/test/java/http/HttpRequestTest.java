package http;

import http.constant.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class HttpRequestTest {

    @Test
    @DisplayName("HttpRequest 생성 테스트")
    public void test1() throws UnsupportedEncodingException {
        String method = "GET";
        String path = "/login";
        String version = "HTTP/1.1";

        List<String> request = List.of("Host: localhost:8080", "Connection: keep-alive",
                "Upgrade-Insecure-Requests: 1", "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
                "Referer: http://localhost:8080/", "Accept-Encoding: gzip, deflate, br, zstd", "Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7",
                "Cookie: testCookie=1212tt11");

        HttpRequest httpRequest = new HttpRequest(method, path, version, request);

        assertThat(httpRequest).isNotNull();
        assertThat(httpRequest.getMethod()).isEqualByComparingTo(HttpMethod.GET);
        assertThat(httpRequest.getPath()).isEqualTo(path);
        assertThat(httpRequest.getSessionIds()).isNotNull();
        assertThat(httpRequest.getSessionIds().containsKey("testCookie")).isTrue();

        assertThat(httpRequest.getBody()).isNull();
    }

    @Test
    @DisplayName("쿼리 파싱 테스트")
    public void test2() throws UnsupportedEncodingException {
        String method = "GET";
        String path = "/login?userId=1&username=myname";
        String version = "HTTP/1.1";

        List<String> request = List.of("Host: localhost:8080", "Connection: keep-alive",
                "Upgrade-Insecure-Requests: 1", "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
                "Referer: http://localhost:8080/", "Accept-Encoding: gzip, deflate, br, zstd", "Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7",
                "Cookie: testCookie=1212tt11");

        HttpRequest httpRequest = new HttpRequest(method, path, version, request);

        assertThat(httpRequest).isNotNull();
        assertThat(httpRequest.getPath()).isEqualTo("/login");
        assertThat(httpRequest.getQueries()).isNotNull();
        assertThat(httpRequest.getQueries().containsKey("username")).isTrue();
        assertThat(httpRequest.getQueries().containsKey("userId")).isTrue();
        assertThat(httpRequest.getQueries().get("username")).isEqualTo("myname");

        assertThat(httpRequest.getBody()).isNull();
    }

    @Test
    @DisplayName("바디 파싱 테스트")
    public void test3() throws UnsupportedEncodingException {
        String method = "POST";
        String path = "/registration";
        String version = "HTTP/1.1";

        List<String> request = List.of("Host: localhost:8080", "Connection: keep-alive",
                "Upgrade-Insecure-Requests: 1", "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
                "Referer: http://localhost:8080/", "Accept-Encoding: gzip, deflate, br, zstd", "Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7",
                "Cookie: testCookie=1212tt11", "content-length: 30", "content-type: application/x-www-form-urlencoded", "", "userId=1&username=myname");

        HttpRequest httpRequest = new HttpRequest(method, path, version, request);

        assertThat(httpRequest).isNotNull();
        assertThat(httpRequest.getPath()).isEqualTo("/registration");
        assertThat(httpRequest.getMethod()).isEqualByComparingTo(HttpMethod.POST);
        assertThat(httpRequest.getQueries()).isNull();
        assertThat(httpRequest.getBody()).isNotNull();
        assertThat(httpRequest.getBody()).isEqualTo("userId=1&username=myname");
    }
}