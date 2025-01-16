package webserver.httpserver.header;

import java.util.HashMap;
import java.util.Map;

public class Cookie {
    public static final Cookie NULL_COOKIE = new NullCookie();
    private final Map<String, String> cookies;

    public Cookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public String getCookie(String key) {
        return cookies.getOrDefault(key, "false");
    }

    private static class NullCookie extends Cookie {
        private NullCookie() {
            super(new HashMap<>());

        }
    }
}
