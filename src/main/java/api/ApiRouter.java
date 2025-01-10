package api;

import api.user.UserCreationHandler;
import global.model.RequestData;
import global.model.LoadResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApiRouter {
    private final List<ApiHandler> handlers = new ArrayList<>();

    public ApiRouter() {
        handlers.add(new UserCreationHandler());
    }

    public LoadResult route(RequestData requestData) throws IOException {
        for (ApiHandler handler : handlers) {
            if (handler.canHandle(requestData)) {
                return handler.handle(requestData);
            }
        }
        return null;
    }
}