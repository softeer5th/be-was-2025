package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Cookie {
    private String name;
    private String value;
    private Long maxAge;
    private String path;
    private boolean httpOnly;
    private LocalDateTime expire;
    private String domain;
    private boolean secure;

    public static String SESSION_COOKIE_NAME = "sid";

    public Cookie() {
        this(SESSION_COOKIE_NAME);
    }

    public Cookie(String name) {
        this.name = name;
        this.value = createCookieValue();

        this.httpOnly = false;
        this.secure = false;
    }


    private String createCookieValue() {
        return Base64.getEncoder().encodeToString(UUID.randomUUID().toString().substring(0, 12).getBytes());
    }

    public String createCookieString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s=%s", name, value));
        if (getPath() != null)
            sb.append(String.format(" ;%s=%s", "Path", getPath()));
        if (getDomain() != null)
            sb.append(String.format(" ;%s=%s", "Domain", getDomain()));
        if (isSecure())
            sb.append(String.format(" ;%s", "Secure"));
        if (getMaxAge() != null)
            sb.append(String.format(" ;%s=%d", "Max-Age", getMaxAge()));
        if (getExpire() != null)
            sb.append(String.format(" ;%s=%s", "Expires", getExpire().format(DateTimeFormatter.RFC_1123_DATE_TIME)));
        if (isHttpOnly())
            sb.append(String.format(" ;%s", "HttpOnly"));

        return sb.toString();
    }

    public static Map<String, String> parse(String cookieString) {
        Map<String, String> ids = new HashMap<>();
        String[] pairs = cookieString.split("; ");
        for(String pair : pairs)  {
            String[] tokens = pair.split("=", 2);
            String name = tokens[0].trim().toLowerCase();
            String value = tokens[1] != null ? tokens[1].trim() : null;
            ids.put(name, value);
        }

        return ids;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public Long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public LocalDateTime getExpire() {
        return expire;
    }

    public void setExpire(LocalDateTime expire) {
        this.expire = expire;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }
}
