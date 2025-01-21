package webserver.http.cookie;

public class Cookie {
    private final String name;
    private final String value;
    private String domain;
    private String path;
    private Long expires;
    private boolean secure;

    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public void setDomain(String domain) { this.domain = domain; }

    public void setPath(String path) { this.path = path; }

    public void setMaxAge(Long expires) { this.expires = expires; }

    public void setSecure(boolean secure) { this.secure = secure; }

    public String getName() { return name; }

    public String getValue() { return value; }

    public String getDomain() { return domain; }

    public String getPath() { return path; }

    public Long getMaxAge() { return expires; }

    public boolean isSecure() { return secure; }

    @Override
    public String toString() {
        StringBuilder cookieBuilder = new StringBuilder();

        cookieBuilder.append(name).append("=").append(value);
        if (domain != null) cookieBuilder.append("; Domain=").append(domain);
        if (path != null) cookieBuilder.append("; Path=").append(path);
        if (expires != null) cookieBuilder.append("; Max-Age=").append(expires);
        if (secure) cookieBuilder.append("; Secure");

        return cookieBuilder.toString();
    }
}
