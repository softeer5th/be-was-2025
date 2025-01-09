package api;

import api.user.UserCreationHandler;
import db.Database;
import model.RequestData;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.load.LoadResult;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class UserCreationHandlerTest {

    private UserCreationHandler userCreationHandler;

    @BeforeEach
    @DisplayName("UserCreationHandler 초기화")
    void setUp() {
        userCreationHandler = new UserCreationHandler();
    }

    @Test
    @DisplayName("GET 메서드와 /create 경로가 주어졌을 때 처리 가능 여부를 확인한다.")
    void handlesGetCreatePath() {
        // given
        RequestData requestData = new RequestData("GET", "/create?userId=mun&password=1234&name=Hee", "", "");

        // when
        boolean result = userCreationHandler.canHandle(requestData);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("POST 메서드일 때 처리 불가 여부를 확인한다.")
    void doesNotHandlePostMethod() {
        // given
        RequestData requestData = new RequestData("POST", "/create?userId=mun", "", "");

        // when
        boolean result = userCreationHandler.canHandle(requestData);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("/create 경로가 아닌 경우 처리 불가 여부를 확인한다.")
    void doesNotHandleInvalidPath() {
        // given
        RequestData requestData = new RequestData("GET", "/unknown?userId=mun", "", "");

        // when
        boolean result = userCreationHandler.canHandle(requestData);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("쿼리 문자열이 없는 요청일 때 null 반환 여부를 확인한다.")
    void returnsNullIfNoQueryString() {
        // given
        RequestData requestData = new RequestData("GET", "/create", "", "");

        // when
        LoadResult loadResult = userCreationHandler.handle(requestData);

        // then
        assertThat(loadResult).isNull();
    }

    @Test
    @DisplayName("필수 파라미터(userId, password, name)가 누락된 경우 null 반환 여부를 확인한다.")
    void returnsNullIfRequiredParamsMissing() {
        // given
        RequestData requestData = new RequestData("GET", "/create?password=1234&name=Hee", "", "");

        // when
        LoadResult loadResult = userCreationHandler.handle(requestData);

        // then
        assertThat(loadResult).isNull();
    }

    @Test
    @DisplayName("정상적인 회원가입 요청 시 리다이렉트 HTML 반환 여부를 확인한다.")
    void returnsRedirectOnValidSignup() {
        // given
        RequestData requestData = new RequestData("GET",
                "/create?userId=mun&password=qwerty&name=Hee Sang",
                "",
                "");

        // when
        LoadResult loadResult = userCreationHandler.handle(requestData);

        // then
        assertThat(loadResult).isNotNull();
        byte[] contentBytes = loadResult.content();
        assertThat(contentBytes).isNotNull();

        String html = new String(contentBytes, StandardCharsets.UTF_8);
        assertThat(html).contains("url=/index.html");
    }

    @Test
    @DisplayName("정상적인 회원가입 요청 시 DB에 유저가 저장되는지 확인한다.")
    void savesUserToDatabase() {
        // given
        RequestData requestData = new RequestData("GET",
                "/create?userId=mun&password=qwerty&name=Hee Sang",
                "",
                "");

        // when
        userCreationHandler.handle(requestData);

        // then
        User savedUser = Database.findUserById("mun");
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getPassword()).isEqualTo("qwerty");
        assertThat(savedUser.getName()).isEqualTo("Hee Sang");
    }
}