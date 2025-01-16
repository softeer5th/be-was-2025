package webserver;

public class Cookie {
    private final String name;
    private final String value;
    private final int maxAge;
    private final String path;

    public Cookie(String name, String value, int maxAge) {
        this.name = name;
        this.value = value;
        this.maxAge = maxAge;
        this.path = "/";
    }

    public Cookie(String name, String value, int maxAge, String path) {
        this.name = name;
        this.value = value;
        this.maxAge = maxAge;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public String toString() {
        return this.name + "=" + this.value + "; Max-Age=" + this.maxAge + "; Path=/;";
    }
}
