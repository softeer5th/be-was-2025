package api;

import model.RequestData;
import webserver.load.LoadResult;

import java.io.IOException;

public interface ApiHandler {
    boolean canHandle(RequestData requestData);

    LoadResult handle(RequestData requestData) throws IOException;
}