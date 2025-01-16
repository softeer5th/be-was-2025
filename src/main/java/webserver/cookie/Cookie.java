package webserver.cookie;

import util.enums.CookieName;

import java.util.HashMap;
import java.util.Map;

public class Cookie {
    private final Map<String, String> cookies = new HashMap<>();

    public Cookie(String cookieString){
        String[] pairs;

        try {
            pairs = cookieString.split(";");
        } catch (ArrayIndexOutOfBoundsException e) {
            pairs = new String[] {cookieString};
        }


        for (String pair : pairs) {
            try {
                String[] tokens = pair.trim().split("=");
                cookies.put(CookieName.isValid(tokens[1]), tokens[1]);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new RuntimeException("Invalid String");
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getValue(String name){
        return cookies.get(name);
    }
}
