package webserver.message.body;

import util.HeterogeneousContainer;

import java.io.InputStream;

public interface BodyParser {
    HeterogeneousContainer parse(HeterogeneousContainer headers, InputStream inputStream);
}
