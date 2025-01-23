package router;

import exception.ClientErrorException;
import exception.ErrorCode;
import handler.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestRouterTest {
    RequestRouter router = new RequestRouter();
    @Test
    @DisplayName("path가 /user/로 시작하면 userRequestHandler를 반환한다.")
    void routeToUserRequestHandler() {
        String path = "/user/create";
        final Handler handler = router.route(path);
        Assertions.assertThat(handler)
                .isInstanceOf(UserRequestHandler.class);
    }

    @Test
    @DisplayName("path가 /index.html이면 DynamicHomeHandler를 반환한다.")
    void routeToDynamicHomeHandler() {
        String path = "/index.html";
        final Handler handler = router.route(path);
        Assertions.assertThat(handler)
                .isInstanceOf(DynamicHomeHandler.class);
    }

    @Test
    @DisplayName("path가 /mypage/index.html이면 MyPageHandler를 반환한다.")
    void routeToMyPageHandler() {
        String path = "/mypage/index.html";
        final Handler handler = router.route(path);
        Assertions.assertThat(handler)
                .isInstanceOf(DynamicFileHandler.class);
    }

    @Test
    @DisplayName("path가 파일 확장자로 끝나면 staticFileHandler를 반환한다.")
    void routeToStaticFileHandler() {
        String path = "/another/index.html";
        final Handler handler = router.route(path);
        Assertions.assertThat(handler)
                .isInstanceOf(StaticFileHandler.class);

    }

    @Test
    @DisplayName("path가 /이면 HomeHandler를 반환한다.")
    void routeToHomeHandler() {
        String path = "/";
        final Handler handler = router.route(path);
        Assertions.assertThat(handler)
                .isInstanceOf(HomeHandler.class);
    }

    @Test
    @DisplayName("라우터에 등록되지 않은 path로 요청이 오면 에러가 발생한다.")
    void route_invalidPath() {
        String path = "/invalid";
        Assertions.assertThatThrownBy(()->router.route(path))
                .isInstanceOf(ClientErrorException.class)
                .hasMessage(ErrorCode.NOT_ALLOWED_PATH.getMessage());
    }
}