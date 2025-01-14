package webserver.header;

import webserver.enums.ParsingConstant;

import java.time.LocalDateTime;

public class SetCookie {
    private final Cookie cookie;
    private String domain;
    private LocalDateTime expires;
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

    public String getName() {
        return cookie.getName();
    }

    public String getValue() {
        return cookie.getValue();
    }

    public String getDomain() {
        return domain;
    }

    public LocalDateTime getExpires() {
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

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(cookie.getName());
        builder.append(ParsingConstant.COOKIE_KEY_VALUE_SEPARATOR.value);
        builder.append(cookie.getValue());
        if (domain != null) {
            builder.append("; Domain=").append(domain);
        }
        if (expires != null) {
            builder.append("; Expires=").append(expires);
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

    public enum SameSite {
        LAX, STRICT, NONE
    }
}
