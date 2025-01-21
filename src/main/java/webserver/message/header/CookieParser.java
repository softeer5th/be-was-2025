package webserver.message.header;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CookieParser {
    private static final Pattern COOKIE_PATTERN = Pattern.compile("[; ]?(?<name>([^=]+))=(?<value>([^;]*))");

    public static Map<String, String> parse(String cookieValue) {
        Matcher matcher = COOKIE_PATTERN.matcher(cookieValue);
        Map<String, String> cookies = new HashMap<>();
        while (matcher.find()) {
            String name = matcher.group("name").trim();
            String values = matcher.group("value");
            cookies.put(name, values);
        }
        return cookies;
    }
}
