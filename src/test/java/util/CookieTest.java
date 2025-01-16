package util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class CookieTest {

    Cookie cookie;

    @BeforeEach
    public void setUp() {
        cookie = new Cookie();
    }

    @Test
    @DisplayName("쿠키 생성 테스트")
    public void test1() {
        cookie.setMaxAge(1800);

        assertThat(cookie).isNotNull();
        assertThat(cookie.getName()).isEqualTo(Cookie.SESSION_COOKIE_NAME);
        assertThat(cookie.getValue()).isBase64();

        assertThat(cookie.getMaxAge()).isEqualTo(1800);
        assertThat(cookie.getPath()).isNull();
    }

    @Test
    @DisplayName("Set-Cookie 필드 문자열 생성 테스트")
    public void test2() {
        cookie.setPath("/");
        cookie.setMaxAge(1800);
        cookie.setHttpOnly(true);

        String cookieString = cookie.createCookieString();

        assertThat(cookieString).isNotNull();
        assertThat(cookieString).contains("sid", "HttpOnly", "Path", "Max-Age");
        assertThat(cookieString).doesNotContain("Secure", "Domain", "Expires");
    }

    @Test
    @DisplayName("Cookie: 필드 값 파싱 테스트")
    public void test3() {
        String cookieString = "sid=123123; test=567567; cookiepair=cookiecookie";

        Map<String, String> pairs = Cookie.parse(cookieString);

        assertThat(pairs).isNotNull();
        assertThat(pairs.containsKey("cookiepair")).isTrue();
        assertThat(pairs.get("cookiepair")).isEqualTo("cookiecookie");
    }
}