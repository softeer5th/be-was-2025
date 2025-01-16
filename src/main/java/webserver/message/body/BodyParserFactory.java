package webserver.message.body;

import java.util.HashMap;
import java.util.Map;

public class BodyParserFactory {
    private static BodyParser defaultParser = new DefaultBodyParser();
    private static Map<String, BodyParser> parsers = new HashMap<>() {{
        put("x-www-form-urlencoded", new URLEncodedParser());
        put("octet-stream", defaultParser);
    }};

    public static BodyParser createFor(String bodyType) {
        return parsers.getOrDefault(bodyType, defaultParser);
    }
}
