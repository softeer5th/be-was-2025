package http;


public class Cookie {
    private final String name;
    private final String value;
    private long maxAge = -1;
    private String path;
    private boolean httpOnly;

    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        StringBuilder cookieString = new StringBuilder();
        cookieString.append(name).append("=").append(value);

        if (maxAge != -1) {
            cookieString.append("; Max-Age=").append(maxAge);
        }
        if (path != null) {
            cookieString.append("; Path=").append(path);
        }
        if (httpOnly) {
            cookieString.append("; HttpOnly");
        }


        return cookieString.toString();
    }
}
