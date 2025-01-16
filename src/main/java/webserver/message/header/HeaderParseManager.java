package webserver.message.header;

import util.HeterogeneousContainer;
import webserver.message.header.parser.AcceptParser;
import webserver.message.header.parser.HeaderParser;

import java.util.LinkedHashMap;

import java.util.Map;
import java.util.Set;

public class HeaderParseManager {
    Map<String, HeaderParser> parsers;

    private HeaderParseManager() {
        this.parsers = Map.of("accept", new AcceptParser());
    }
    private static final HeaderParseManager INSTANCE = new HeaderParseManager();

    public static HeaderParseManager getInstance() {
        return INSTANCE;
    }

    public HeterogeneousContainer parse(Map<String, String> headers) {
        Set<Map.Entry<String, String>> entries = headers.entrySet();
        HeterogeneousContainer container = new HeterogeneousContainer(new LinkedHashMap<>());
        for (Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue();
            HeaderParser parser = parsers.get(key);
            if (parser != null) {
                parser.parse(container, value);
            }
        }
        return container;
    }
}
