package webserver.header;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHeaderTest {
    private RequestHeader requestHeader;

    @BeforeEach
    void setUp() {
        requestHeader = new RequestHeader();
    }

    @Test
    @DisplayName("쿠키 1개 추가")
    void addCookie() {
        // given
        String cookieName = "sessionId";
        String cookieValue = "abc123";

        // when
        requestHeader.addCookie(new Cookie(cookieName, cookieValue));

        // then
        assertThat(requestHeader.getCookie(cookieName)).extracting(Cookie::getValue).isEqualTo(cookieValue);
    }

    @Test
    @DisplayName("쿠키 여러개 추가")
    void addCookies() {
        // given
        List<Cookie> cookies = List.of(
                new Cookie("sessionId", "abc123"),
                new Cookie("userId", "user1")
        );

        // when
        requestHeader.addCookies(cookies);

        // then
        assertThat(requestHeader.getCookie("sessionId")).extracting(Cookie::getValue).isEqualTo("abc123");
        assertThat(requestHeader.getCookie("userId")).extracting(Cookie::getValue).isEqualTo("user1");
    }

    @Test
    @DisplayName("중복 쿠키 추가 시 마지막 값으로 덮어씀")
    void addCookiesOverwrite() {
        // given
        // when
        requestHeader.addCookie(new Cookie("sessionId", "abc"));
        requestHeader.addCookie(new Cookie("sessionId", "123"));

        // then
        assertThat(requestHeader.getCookie("sessionId")).extracting(Cookie::getValue).isEqualTo("123");
    }

    @Test
    @DisplayName("헤더 1개 추가")
    void setHeader() {
        // given
        String headerName = "Content-Type";
        String headerValue = "application/json";

        // when
        requestHeader.setHeader(headerName, headerValue);

        // then
        assertThat(requestHeader.getHeader(headerName)).isEqualTo(headerValue);
    }

    @Test
    @DisplayName("대소문자 섞어서 헤더 값 조회")
    void containsHeader() {
        // given
        String headerName = "CoNtEnt-Type";
        String headerValue = "application/json";
        requestHeader.setHeader(headerName, headerValue);

        // when
        boolean contains = requestHeader.containsHeader("cOntent-typE");

        // then
        assertThat(contains).isTrue();
    }


}