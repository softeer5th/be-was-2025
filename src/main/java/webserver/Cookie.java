package webserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    public static Map<String, String> parseCookies(String cookieString) {
        Map<String, String> cookies = new HashMap<>();

        String[] cookiesArray = cookieString.split(";");
        for (String cookie : cookiesArray) {
            String[] cookieParts = cookie.split("=");
            if (cookieParts.length != 2) {
                throw new HTTPExceptions.Error400("400 Bad Request: Invalid cookie format");
            }
            String name = cookieParts[0].trim();
            String value = cookieParts[1].trim();
            cookies.put(name, value);
        }
        return cookies;
    }
}
