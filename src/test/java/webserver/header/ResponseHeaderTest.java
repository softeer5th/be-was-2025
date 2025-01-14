package webserver.header;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseHeaderTest {
    private ResponseHeader responseHeader;

    @BeforeEach
    void setUp() {
        responseHeader = new ResponseHeader();
    }

    @Test
    @DisplayName("대소문자 섞어서 헤더 추가")
    void addHeader() {
        String headerName = "conteNt-Type";
        String headerValue = "application/json";

        responseHeader.setHeader(headerName, headerValue);

        assertThat(responseHeader.getHeader("Content-TypE")).isEqualTo(headerValue);
    }

    @Test
    @DisplayName("대소문자 섞어서 헤더 조회")
    void containsHeader() {
        String headerName = "Content-Type";
        String headerValue = "application/json";
        responseHeader.setHeader(headerName, headerValue);

        boolean contains = responseHeader.containsHeader("cOntent-tYpe");

        assertThat(contains).isTrue();
    }

    @Test
    @DisplayName("Set-Cookie 헤더 추가")
    void addSetCookie() {
        SetCookie setCookie = new SetCookie("sessionId", "abc123");

        responseHeader.addSetCookie(setCookie);

        assertThat(responseHeader.getSetCookies()).contains(setCookie);
    }

    @Test
    @DisplayName("여러 Set-Cookie 헤더 추가")
    void addMultipleSetCookies() {
        SetCookie setCookie1 = new SetCookie("sessionId", "abc123");
        SetCookie setCookie2 = new SetCookie("userId", "user1");

        responseHeader.addSetCookie(setCookie1);
        responseHeader.addSetCookie(setCookie2);

        assertThat(responseHeader.getSetCookies()).containsExactlyInAnyOrder(setCookie1, setCookie2);
    }

}