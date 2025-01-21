package fixtureUtil;

import handler.Handler;
import router.Router;

public class ServerExceptionRouter implements Router {
    @Override
    public Handler route(String path) {
        return new TestHandlerThrowsServerException();
    }
}
