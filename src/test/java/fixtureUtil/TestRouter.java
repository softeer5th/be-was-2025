package fixtureUtil;

import handler.Handler;
import router.Router;

public class TestRouter implements Router {

    @Override
    public Handler route(String path) {
        return new TestHandlerReturnsHttpResponse();
    }
}
