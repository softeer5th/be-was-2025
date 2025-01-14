package router;

import exception.BaseException;
import exception.HttpErrorCode;
import handler.Handler;
import handler.UserRegisterHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RequestRouterTest {

    private static final String VALID_PATH = "/users/register";
    private static final String INVALID_PATH = "invalid/path";

    @Test
    @DisplayName("라우팅 성공")
    void testRouteWithValidPath() {
        RequestRouter router = new RequestRouter();

        Handler handler = router.route(VALID_PATH);
        assertNotNull(handler);
        assertInstanceOf(UserRegisterHandler.class, handler);
    }

    @Test
    @DisplayName("라우팅 실패")
    void testRouteWithInvalidPath() {
        RequestRouter router = new RequestRouter();

        BaseException baseException = assertThrows(BaseException.class, () -> router.route(INVALID_PATH));
        assertEquals(baseException.getMessage(), HttpErrorCode.NOT_FOUND_PATH.getMessage());
    }
}
