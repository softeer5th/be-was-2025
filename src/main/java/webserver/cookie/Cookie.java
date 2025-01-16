package webserver.cookie;

import util.enums.CookieName;

import java.util.HashMap;
import java.util.Map;

public class Cookie {
    private final Map<String, String> cookies = new HashMap<>();

    public Cookie(String cookieString){
        try {
            String[] pairs = cookieString.split(";");
            for (String pair : pairs) {
                String[] tokens = pair.trim().split("=");
                cookies.put(CookieName.valueOf(tokens[0]).getName(), tokens[1]);
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid cookie");
        }
    }

    public String getValue(String name){
        return cookies.get(name);
    }
}
