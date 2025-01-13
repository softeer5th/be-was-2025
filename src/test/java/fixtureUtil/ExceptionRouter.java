package fixtureUtil;

import handler.Handler;
import router.Router;

public class ExceptionRouter implements Router {
    @Override
    public Handler route(String path) {
        return new TestHandlerThrowsClientException();
    }
}
