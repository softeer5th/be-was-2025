package router;

import handler.Handler;

public interface Router {

    Handler route(String path);
}