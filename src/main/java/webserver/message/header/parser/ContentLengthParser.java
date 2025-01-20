package webserver.message.header.parser;

import util.HeterogeneousContainer;
import webserver.exception.HTTPException;

public class ContentLengthParser implements HeaderParser {
    @Override
    public void parse(HeterogeneousContainer headers, String value) {
        try {
            Integer res = Integer.parseInt(value.trim());
            headers.put("content-length", res, Integer.class);
        } catch (NumberFormatException e) {
            throw new HTTPException.Builder()
                    .causedBy(ContentLengthParser.class)
                    .badRequest(e.getMessage());
        }
    }
}
