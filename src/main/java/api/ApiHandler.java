package api;

import global.model.HttpRequest;
import global.model.LoadResult;

import java.io.IOException;

public interface ApiHandler {
    boolean canHandle(HttpRequest httpRequest);

    LoadResult handle(HttpRequest httpRequest) throws IOException;
}