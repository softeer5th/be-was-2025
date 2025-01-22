package api;

import api.article.*;
import api.user.*;
import global.model.HttpRequest;
import global.model.LoadResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApiRouter {
    private final List<ApiHandler> handlers = new ArrayList<>();

    public ApiRouter() {
        handlers.add(new SignUpHandler());
        handlers.add(new LoginHandler());
        handlers.add(new LogoutHandler());
        handlers.add(new ValidateHandler());
        handlers.add(new UpdateUserHandler());
        handlers.add(new ImageCreateHandler());
        handlers.add(new ImageDeleteHandler());
        handlers.add(new ArticleCreateHandler());
        handlers.add(new ArticleWithCommentsPaginationHandler());
        handlers.add(new CommentCreateHandler());
    }

    public LoadResult route(HttpRequest httpRequest) throws IOException {
        for (ApiHandler handler : handlers) {
            if (handler.canHandle(httpRequest)) {
                return handler.handle(httpRequest);
            }
        }
        return null;
    }
}