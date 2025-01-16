package webserver.header;

import webserver.enums.ParsingConstant;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

// Set-Cooki 헤더와 관련된 정보
public class SetCookie {
    private final Cookie cookie;
    private String domain;
    private ZonedDateTime expires;
    private boolean httpOnly;
    private Integer maxAge;
    private String path;
    private SameSite sameSite;
    private boolean secure;

    public SetCookie(String name, String value) {
        this.cookie = new Cookie(name, value);
        httpOnly = true;
        sameSite = SameSite.LAX;
        secure = false;
    }

    // 쿠키를 만료시키는 Set-Cookie 헤더를 만들어주는 정적 팩토리 메서드
    public static SetCookie expireCookie(String cookieName) {
        SetCookie setCookie = new SetCookie(cookieName, "");
        setCookie.setMaxAge(0);
        setCookie.expires = ZonedDateTime.now().minusYears(1);
        return setCookie;
    }

    // 브라우저를 끌 때 까지 유지되는 session scope 쿠키를 위한 Set-Cookie 헤더를 만들어주는 정적 팩토리 메서드
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

    // Http Header 형식에 맞게 문자열로 변환
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

    public enum SameSite {
        LAX, STRICT, NONE
    }
}
