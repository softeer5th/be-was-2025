package webserver.httpserver.header;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Cookie {
    public static final Cookie NULL_COOKIE = new NullCookie();
    private final Map<String, String> pairCookies = new HashMap<>();
    private String name;
    private String value;
    private String domain;
    private String path;
    private Integer maxAge;
    private LocalDateTime expires;
    private boolean secure;
    private boolean httpOnly;
    private String sameSite;

    /**
     * 새로운 쿠키를 생성하는 생성자.
     * Set-Cookie로 새로운 쿠키를 클라이언트에게 지정할 경우, 적당한 쿠키를 지정하기 위한 생성자.
     */
    public Cookie() {
    }

    /**
     * 주어진 쿠키를 파싱하여 저장하는 생성자.
     * 서버가 Request로 Cookie 헤더를 전달받았을 시, 파싱 후 Cookie 객체 형태로 저장.
     * @param values
     */
    public Cookie(String values) {
        String[] cookieParts = values.split(";");
        for (String cookiePart : cookieParts) {
            String[] keyValue = cookiePart.split("=");
            keyValue[0] = keyValue[0].toLowerCase();
            if (keyValue.length == 2) {
                pairCookies.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
    }

    public void setValue(String key, String value) {
        this.name = key;
        this.value = value;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public void setExpires(LocalDateTime expires) {
        this.expires = expires;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public void setSameSite(String sameSite) {
        this.sameSite = sameSite;
    }

    public String getCookie(String key) {
        return pairCookies.getOrDefault(key, "false");
    }



    /**
     * 쿠키를 문자열로 바꾸는 함수
     * @return 쿠키가 Set-Cookie 의 value 로 들어갈 때 사용되는 문자열
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(name).append("=").append(value);
        if (domain != null) {
            builder.append("; Domain=").append(domain);
        }
        if (path != null) {
            builder.append("; Path=").append(path);
        }
        if (maxAge != null) {
            builder.append("; Max-Age=").append(maxAge);
        }
        if (expires != null) {
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            ZonedDateTime zonedDateTime = expires.atZone(zoneId);
            ZonedDateTime gmtDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("GMT"));
            builder.append("; Expires=").append(DateTimeFormatter.RFC_1123_DATE_TIME.format(gmtDateTime));
        }
        if (secure) {
            builder.append("; Secure");
        }
        if (httpOnly) {
            builder.append("; HttpOnly");
        }
        if (sameSite != null) {
            builder.append("; SameSite=").append(sameSite);
        }
        return builder.toString();
    }


    private static class NullCookie extends Cookie {
        private NullCookie() {
        }
    }
}
