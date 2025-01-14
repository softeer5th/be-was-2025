package http.router;

import http.enums.HttpMethod;
import http.handler.BadRequestHandler;
import http.handler.Handler;
import http.handler.StaticResourceHandler;
import http.handler.UserHandler;
import http.request.HttpRequest;
import http.request.TargetInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RouterTest {

    private Router router;
    private HttpRequest mockRequest;

    @BeforeEach
    public void setUp() {
        router = new Router();
        mockRequest = mock(HttpRequest.class);
    }

    @Test
    @DisplayName("정적 리소스 요청 라우팅 테스트")
    public void testRouteToStaticResource() {
        when(mockRequest.isInvalid()).thenReturn(false);
        when(mockRequest.getMethod()).thenReturn(HttpMethod.GET);
        when(mockRequest.getTarget()).thenReturn(new TargetInfo("/main", null));

        Handler handler = router.route(mockRequest);
        assertTrue(handler instanceof StaticResourceHandler);
    }

    @Test
    @DisplayName("유저 핸들러 요청 라우팅 테스트")
    public void testRouteToUserHandler() {
        when(mockRequest.isInvalid()).thenReturn(false);
        when(mockRequest.getMethod()).thenReturn(HttpMethod.GET);
        when(mockRequest.getTarget()).thenReturn(new TargetInfo("/user/create", null));

        Handler handler = router.route(mockRequest);
        assertInstanceOf(UserHandler.class, handler);
    }

    @Test
    @DisplayName("잘못된 요청 라우팅 테스트")
    public void testRouteToBadRequestHandler() {
        when(mockRequest.isInvalid()).thenReturn(true);

        Handler handler = router.route(mockRequest);
        assertInstanceOf(BadRequestHandler.class, handler);
    }

    @Test
    @DisplayName("정적 파일 요청 라우팅 테스트")
    public void testStaticFileRequest() {
        when(mockRequest.isInvalid()).thenReturn(false);
        when(mockRequest.getMethod()).thenReturn(HttpMethod.GET);
        when(mockRequest.getTarget()).thenReturn(new TargetInfo("/styles/main.css", null));

        Handler handler = router.route(mockRequest);
        assertInstanceOf(StaticResourceHandler.class, handler);
    }

    @Test
    @DisplayName("잘못된 경로 요청 라우팅 테스트")
    public void testInvalidPathRequest() {
        when(mockRequest.isInvalid()).thenReturn(false);
        when(mockRequest.getMethod()).thenReturn(HttpMethod.GET);
        when(mockRequest.getTarget()).thenReturn(new TargetInfo("/invalid/path", null));

        Handler handler = router.route(mockRequest);
        assertInstanceOf(StaticResourceHandler.class, handler);
    }
}