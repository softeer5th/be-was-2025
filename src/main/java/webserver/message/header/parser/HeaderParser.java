package webserver.message.header.parser;

import util.HeterogeneousContainer;

public interface HeaderParser {
    void parse(HeterogeneousContainer headers, String value);
}
