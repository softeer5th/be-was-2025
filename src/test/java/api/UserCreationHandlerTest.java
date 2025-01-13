package api;

import api.user.UserCreationHandler;
import db.Database;
import global.exception.ErrorCode;
import global.model.CommonResponse;
import global.model.HttpRequest;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import global.model.LoadResult;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static global.util.JsonUtil.toJson;

class UserCreationHandlerTest {

    private UserCreationHandler userCreationHandler;

    @BeforeEach
    @DisplayName("초기화")
    void setUp() {
        userCreationHandler = new UserCreationHandler();
        Database.clear();
    }

    @Test
    @DisplayName("GET 메서드와 /api/create 경로가 주어졌을 때 처리 가능 여부를 확인한다.")
    void handlesGetCreatePath() {
        // given
        HttpRequest httpRequest = new HttpRequest("GET", "/api/create?userId=mun&password=1234&name=Hee", "", "");

        // when
        boolean result = userCreationHandler.canHandle(httpRequest);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("POST 메서드일 때 처리 불가 여부를 확인한다.")
    void doesNotHandlePostMethod() {
        // given
        HttpRequest httpRequest = new HttpRequest("POST", "/api/create?userId=mun", "", "");

        // when
        boolean result = userCreationHandler.canHandle(httpRequest);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("/api/create 경로가 아닌 경우 처리 불가 여부를 확인한다.")
    void doesNotHandleInvalidPath() {
        // given
        HttpRequest httpRequest = new HttpRequest("GET", "/unknown?userId=mun", "", "");

        // when
        boolean result = userCreationHandler.canHandle(httpRequest);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("쿼리 문자열이 없는 요청일 때 실패 응답을 반환한다.")
    void returnsFailureIfNoQueryString() {
        // given
        HttpRequest httpRequest = new HttpRequest("GET", "/api/create", "", "");

        // when
        LoadResult loadResult = userCreationHandler.handle(httpRequest);

        // then
        assertThat(loadResult).isNotNull();
        String actualJson = new String(loadResult.content(), StandardCharsets.UTF_8);
        String expectedJson = toJson(new CommonResponse(false, "SIGNUP-03", "유효하지 않은 사용자 입력입니다.", null));

        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    @DisplayName("필수 파라미터(userId, password, name)가 누락된 경우 실패 응답을 반환한다.")
    void returnsFailureIfRequiredParamsMissing() {
        // given
        HttpRequest httpRequest = new HttpRequest("GET", "/api/create?userId=mun&name=Hee", "", "");

        // when
        LoadResult loadResult = userCreationHandler.handle(httpRequest);

        // then
        assertThat(loadResult).isNotNull();
        String actualJson = new String(loadResult.content(), StandardCharsets.UTF_8);
        String expectedJson = toJson(new CommonResponse(false, "SIGNUP-03", "유효하지 않은 사용자 입력입니다.", null));

        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    @DisplayName("정상적인 회원가입 요청 시 성공 응답을 반환한다.")
    void returnsSuccessOnValidSignup() {
        // given
        HttpRequest httpRequest = new HttpRequest("GET", "/api/create?userId=mun&password=qwerty&name=Hee", "", "");

        // when
        LoadResult loadResult = userCreationHandler.handle(httpRequest);

        // then
        assertThat(loadResult).isNotNull();
        CommonResponse expectedResponse = new CommonResponse(true, null, null, null);
        String expectedJson = toJson(expectedResponse);

        String actualJson = new String(loadResult.content(), StandardCharsets.UTF_8);
        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    @DisplayName("중복된 userId가 주어졌을 때 실패 응답을 반환한다.")
    void returnsFailureOnDuplicateUserId() {
        // given
        User existingUser = User.of("mun", "1234", "Hee");
        Database.addUser(existingUser);

        HttpRequest httpRequest = new HttpRequest("GET", "/api/create?userId=mun&password=5678&name=NewHee", "", "");

        // when
        LoadResult loadResult = userCreationHandler.handle(httpRequest);

        // then
        assertThat(loadResult).isNotNull();
        CommonResponse expectedResponse = new CommonResponse(false, "SIGNUP-01", ErrorCode.USER_ALREADY_EXISTS.getMessage(), null);
        String expectedJson = toJson(expectedResponse);

        String actualJson = new String(loadResult.content(), StandardCharsets.UTF_8);
        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    @DisplayName("중복된 name이 주어졌을 때 실패 응답을 반환한다.")
    void returnsFailureOnDuplicateName() {
        // given
        User existingUser = User.of("user123", "1234", "Hee");
        Database.addUser(existingUser);

        HttpRequest httpRequest = new HttpRequest("GET", "/api/create?userId=newUser&password=5678&name=Hee", "", "");

        // when
        LoadResult loadResult = userCreationHandler.handle(httpRequest);

        // then
        assertThat(loadResult).isNotNull();
        CommonResponse expectedResponse = new CommonResponse(false, "SIGNUP-02", ErrorCode.DUPLICATED_NAME.getMessage(), null);
        String expectedJson = toJson(expectedResponse);

        String actualJson = new String(loadResult.content(), StandardCharsets.UTF_8);
        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    @DisplayName("정상적인 회원가입 요청 시 DB에 유저가 저장되는지 확인한다.")
    void savesUserToDatabase() {
        // given
        HttpRequest httpRequest = new HttpRequest("GET", "/api/create?userId=mun&password=qwerty&name=Hee", "", "");

        // when
        userCreationHandler.handle(httpRequest);

        // then
        User savedUser = Database.findUserById("mun");
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getPassword()).isEqualTo("qwerty");
        assertThat(savedUser.getName()).isEqualTo("Hee");
    }
}