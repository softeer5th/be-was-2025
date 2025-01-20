package webserver.cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.enums.CookieName;

import java.util.HashMap;
import java.util.Map;

public class Cookie {
    private static final Logger logger = LoggerFactory.getLogger(Cookie.class);
    private final Map<String, String> cookies = new HashMap<>();

    public Cookie(String cookieString){
        String[] pairs = cookieString.split(";");

        for (String pair : pairs) {
            try {
                String[] tokens = pair.trim().split("=", 2);
                String name = CookieName.validate(tokens[0]);

                if(pair.endsWith("=")) {
                    logger.error("empty cookie");
                    continue;
                }

                String value = tokens[1];

                if (value.length() > 2 && value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }

                cookies.put(name, value);
            } catch (ArrayIndexOutOfBoundsException e) {
                logger.error("Invalid String");
            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public String getValue(String name){
        return cookies.get(name);
    }
}
