package webserver.cookie;

import util.enums.CookieName;

import java.util.HashMap;
import java.util.Map;

public class Cookie {
    private final Map<String, String> cookies = new HashMap<>();

    public Cookie(String cookieString){
        String[] pairs = cookieString.split(";");

        for (String pair : pairs) {
            try {
                String[] tokens = pair.trim().split("=", 2);
                String name = CookieName.validate(tokens[0]);

                if(tokens.length == 1 && pair.endsWith("=")) {throw new RuntimeException("empty cookie");}

                String value = tokens[1];

                if (value.length() > 2 && value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }

                cookies.put(name, value);
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
