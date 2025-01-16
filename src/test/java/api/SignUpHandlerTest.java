package api;

import api.user.SignUpHandler;
import db.Database;
import global.exception.ErrorCode;
import global.model.CommonResponse;
import global.model.HttpRequest;
import global.model.LoadResult;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static global.util.JsonUtil.toJson;
import static org.assertj.core.api.Assertions.assertThat;

class SignUpHandlerTest {

    private SignUpHandler signUpHandler;

    @BeforeEach
    @DisplayName("초기화")
    void setUp() {
        signUpHandler = new SignUpHandler();
        Database.clear();
    }

    @Test
    @DisplayName("POST + /api/create 경로일 때 canHandle이 true를 반환한다.")
    void canHandleWithPostCreatePath() {
        // given
        HttpRequest httpRequest = new HttpRequest(
                "POST",
                "/api/create",
                null,
                ""
        );

        // when
        boolean result = signUpHandler.canHandle(httpRequest);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("GET 메서드일 때 canHandle이 false를 반환한다.")
    void cannotHandleGetMethod() {
        // given
        HttpRequest httpRequest = new HttpRequest(
                "GET",
                "/api/create",
                null,
                ""
        );

        // when
        boolean result = signUpHandler.canHandle(httpRequest);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("/api/create가 아닌 경로일 때 canHandle이 false를 반환한다.")
    void cannotHandleInvalidPath() {
        // given
        HttpRequest httpRequest = new HttpRequest(
                "POST",
                "/api/unknown",
                null,
                ""
        );

        // when
        boolean result = signUpHandler.canHandle(httpRequest);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("필수 필드(userId, password, name)가 누락되면 실패 응답을 반환한다.")
    void returnsFailureIfRequiredFieldsMissing() {
        // given
        String requestBody = """
            {
              "userId": "testUser"
            }
            """;

        HttpRequest httpRequest = new HttpRequest(
                "POST",
                "/api/create",
                null,
                requestBody
        );

        // when
        LoadResult loadResult = signUpHandler.handle(httpRequest);

        // then
        assertThat(loadResult).isNotNull();
        assertThat(loadResult.contentType()).isEqualTo("application/json");

        String actualJson = new String(loadResult.content(), StandardCharsets.UTF_8);

        CommonResponse expectedResponse = new CommonResponse(
                false,
                ErrorCode.INVALID_USER_INPUT.getCode(),
                ErrorCode.INVALID_USER_INPUT.getMessage(),
                null
        );
        String expectedJson = toJson(expectedResponse);

        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    @DisplayName("DB에 이미 같은 userId가 있으면 실패 응답을 반환한다.")
    void returnsFailureIfUserIdAlreadyExists() {
        // given
        Database.addUser(new User("testUser", "1234", "SameName", null));

        String requestBody = """
            {
              "userId": "testUser",
              "password": "qwerty",
              "name": "NewName"
            }
            """;

        HttpRequest httpRequest = new HttpRequest(
                "POST",
                "/api/create",
                null,
                requestBody
        );

        // when
        LoadResult loadResult = signUpHandler.handle(httpRequest);

        // then
        assertThat(loadResult).isNotNull();
        assertThat(loadResult.contentType()).isEqualTo("application/json");

        String actualJson = new String(loadResult.content(), StandardCharsets.UTF_8);

        CommonResponse expectedResponse = new CommonResponse(
                false,
                ErrorCode.USER_ALREADY_EXISTS.getCode(),
                ErrorCode.USER_ALREADY_EXISTS.getMessage(),
                null
        );
        String expectedJson = toJson(expectedResponse);

        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    @DisplayName("DB에 이미 같은 name이 있으면 실패 응답을 반환한다.")
    void returnsFailureIfNameAlreadyExists() {
        // given
        Database.addUser(new User("someUser", "1234", "Hee", null));

        String requestBody = """
            {
              "userId": "newUser",
              "password": "qwerty",
              "name": "Hee"
            }
            """;

        HttpRequest httpRequest = new HttpRequest(
                "POST",
                "/api/create",
                null,
                requestBody
        );

        // when
        LoadResult loadResult = signUpHandler.handle(httpRequest);

        // then
        assertThat(loadResult).isNotNull();
        assertThat(loadResult.contentType()).isEqualTo("application/json");

        String actualJson = new String(loadResult.content(), StandardCharsets.UTF_8);

        CommonResponse expectedResponse = new CommonResponse(
                false,
                ErrorCode.DUPLICATED_NAME.getCode(),
                ErrorCode.DUPLICATED_NAME.getMessage(),
                null
        );
        String expectedJson = toJson(expectedResponse);

        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    @DisplayName("정상적인 회원가입 요청이면 리다이렉트 응답을 반환한다.")
    void returnsRedirectOnValidSignup() {
        // given
        String requestBody = """
            {
              "userId": "mun",
              "password": "qwerty",
              "name": "Hee"
            }
            """;

        HttpRequest httpRequest = new HttpRequest(
                "POST",
                "/api/create",
                null,
                requestBody
        );

        // when
        LoadResult loadResult = signUpHandler.handle(httpRequest);

        // then
        assertThat(loadResult).isNotNull();
        assertThat(loadResult.contentType()).isEqualTo("redirect");
        assertThat(loadResult.path()).isEqualTo("/index.html");
        assertThat(loadResult.content()).isNull();
    }

    @Test
    @DisplayName("정상적인 회원가입 요청 시 DB에 유저가 저장된다.")
    void savesUserToDatabaseOnValidSignup() {
        // given
        String requestBody = """
            {
              "userId": "newUser",
              "password": "1234",
              "name": "NewName"
            }
            """;

        HttpRequest httpRequest = new HttpRequest(
                "POST",
                "/api/create",
                null,
                requestBody
        );

        // when
        signUpHandler.handle(httpRequest);

        // then
        User savedUser = Database.findUserById("newUser");
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getPassword()).isEqualTo("1234");
        assertThat(savedUser.getName()).isEqualTo("NewName");
    }
}