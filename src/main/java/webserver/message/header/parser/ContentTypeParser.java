package webserver.message.header.parser;

import util.HeterogeneousContainer;
import webserver.enumeration.HTTPContentType;

public class ContentTypeParser implements HeaderParser {
    @Override
    public void parse(HeterogeneousContainer headers, String value) {
        if (HTTPContentType.isSupported(value)) {
            throw new IllegalArgumentException("Unsupported content type");
        }
        headers.put("accept", HTTPContentType.fromFullType(value), HTTPContentType.class);
    }
}
