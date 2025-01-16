package webserver.message.body;

import util.HeterogeneousContainer;
import webserver.message.HTTPRequest;

import java.io.InputStream;

public interface BodyParser {
    HeterogeneousContainer parse(HTTPRequest request, InputStream inputStream);
}
