package webserver.message.body;

import util.HeterogeneousContainer;

import java.io.BufferedInputStream;

public interface BodyParser {
    HeterogeneousContainer parse(HeterogeneousContainer headers, BufferedInputStream inputStream);
}
