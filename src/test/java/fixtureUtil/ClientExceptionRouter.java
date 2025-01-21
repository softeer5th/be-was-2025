package fixtureUtil;

import handler.Handler;
import router.Router;

public class ClientExceptionRouter implements Router {
    @Override
    public Handler route(String path) {
        return new TestHandlerThrowsClientException();
    }
}
