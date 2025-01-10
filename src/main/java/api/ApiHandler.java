package api;

import global.model.RequestData;
import global.model.LoadResult;

import java.io.IOException;

public interface ApiHandler {
    boolean canHandle(RequestData requestData);

    LoadResult handle(RequestData requestData) throws IOException;
}