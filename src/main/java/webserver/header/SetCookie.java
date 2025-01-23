package webserver.header;

import webserver.enums.ParsingConstant;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Set-Cookie 헤더와 관련된 정보
 */
public class SetCookie {
    private final Cookie cookie;
    private String domain;
    private ZonedDateTime expires;
    private boolean httpOnly;
    private Integer maxAge;
    private String path;
    private SameSite sameSite;
    private boolean secure;

    /**
     * 쿠키 이름과 값을 받아 쿠키를 생성
     *
     * @param name  쿠키 이름
     * @param value 쿠키 값
     */
    public SetCookie(String name, String value) {
        this.cookie = new Cookie(name, value);
        httpOnly = true;
        sameSite = SameSite.LAX;
        secure = false;
        path = "/";
    }

    /**
     * 쿠키를 만료시키는 Set-Cookie 를 생성
     *
     * @param cookieName 만료시킬 쿠키 이름
     * @return 만료시키는 Set-Cookie
     */
    public static SetCookie expireCookie(String cookieName) {
        SetCookie setCookie = new SetCookie(cookieName, "");
        setCookie.setMaxAge(0);
        setCookie.expires = ZonedDateTime.now().minusYears(1);
        return setCookie;
    }

    /**
     * session scope 쿠키를 위한 Set-Cookie 헤더를 생성
     *
     * @param cookieName  쿠키 이름
     * @param cookieValue 쿠키 값
     * @return session scope 쿠키를 위한 Set-Cookie 헤더
     */
    public static SetCookie createSessionCookie(String cookieName, String cookieValue) {
        SetCookie setCookie = new SetCookie(cookieName, cookieValue);
        return setCookie;
    }

    public String getName() {
        return cookie.getName();
    }

    public String getValue() {
        return cookie.getValue();
    }

    public String getDomain() {
        return domain;
    }

    public ZonedDateTime getExpires() {
        return expires;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public String getPath() {
        return path;
    }

    public SameSite getSameSite() {
        return sameSite;
    }

    public boolean isSecure() {
        return secure;
    }

    /**
     * Set-Cookie를 Http Header 형식으로 변환
     *
     * @return Http Header 형식의 Set-Cookie
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(cookie.getName());
        builder.append(ParsingConstant.COOKIE_KEY_VALUE_SEPARATOR.value);
        builder.append(cookie.getValue());
        if (domain != null) {
            builder.append("; Domain=").append(domain);
        }
        if (expires != null) {
            builder.append("; Expires=").append(formatExpires());
        }
        if (httpOnly) {
            builder.append("; HttpOnly");
        }
        if (maxAge != null) {
            builder.append("; Max-Age=").append(maxAge);
        }
        if (path != null) {
            builder.append("; Path=").append(path);
        }
        if (sameSite != null) {
            builder.append("; SameSite=").append(sameSite);
        }
        if (secure) {
            builder.append("; Secure");
        }
        return builder.toString();
    }

    // Expires 필드를 RFC_1123 날짜 형식에 맞게 변환
    private String formatExpires() {
        return expires.withZoneSameInstant(ZoneId.of("GMT")).format(DateTimeFormatter.RFC_1123_DATE_TIME);
    }

    /**
     * 쿠키의 SameSite 속성
     */
    public enum SameSite {
        /**
         * Lax<br>
         * First-Party 쿠키 허용<br>
         * Third-Party 쿠키는 Top-Level Navigation, 안전한 메서드 요청에만 전송
         */
        LAX,
        /**
         * Strict<br>
         * First-Party 쿠키만 허용
         */
        STRICT,
        /**
         * None<br>
         * 모두 허용
         */
        NONE
    }
}
