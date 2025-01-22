package request;

import exception.ClientErrorException;
import exception.ErrorCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserCreateRequestTest {
    @Test
    @DisplayName("회원가입 요청 form에서 userId 필드가 누락될 경우 에러가 발생한다.")
    void userCreateRequest_missingUserIdField() {
        final String email = "email";
        final String password = "password";
        final String nickname = "nickname";
        // 한글 URI.create
        final String invalidForm= String.format("email=%s&password=%s&nickname=%s",  email, password, nickname);

        Assertions.assertThatThrownBy(() -> UserCreateRequest.of(invalidForm))
                .isInstanceOf(ClientErrorException.class)
                .hasMessage(ErrorCode.MISSING_FIELD.getMessage());
    }

    @Test
    @DisplayName("회원가입 요청 form에서 email 필드가 누락될 경우 에러가 발생한다.")
    void userCreateRequest_missingEmailField() {
        final String userId = "id";
        final String password = "password";
        final String nickname = "nickname";
        // 한글 URI.create
        final String invalidForm = String.format("userId=%s&password=%s&nickname=%s", userId, password, nickname);

        Assertions.assertThatThrownBy(() -> UserCreateRequest.of(invalidForm))
                .isInstanceOf(ClientErrorException.class)
                .hasMessage(ErrorCode.MISSING_FIELD.getMessage());
    }


    @Test
    @DisplayName("회원가입 요청 form에서 password 필드가 누락될 경우 에러가 발생한다.")
    void userCreateRequest_missingPasswordField() {
        final String userId = "id";
        final String email = "email";
        final String nickname = "nickname";
        // 한글 URI.create
        final String invalidForm = String.format("userId=%s&email=%s&nickname=%s", userId, email, nickname);

        Assertions.assertThatThrownBy(() -> UserCreateRequest.of(invalidForm))
                .isInstanceOf(ClientErrorException.class)
                .hasMessage(ErrorCode.MISSING_FIELD.getMessage());
    }


    @Test
    @DisplayName("회원가입 요청 form에서 nickname 필드가 누락될 경우 에러가 발생한다.")
    void userCreateRequest_missingNicknameField() {
        final String userId = "id";
        final String email = "email";
        final String password = "password";

        // 한글 URI.create
        final String invalidForm = String.format("userId=%s&email=%s&password=%s&==", userId, email, password);

        Assertions.assertThatThrownBy(() -> UserCreateRequest.of(invalidForm))
                .isInstanceOf(ClientErrorException.class)
                .hasMessage(ErrorCode.MISSING_FIELD.getMessage());
    }



    @Test
    @DisplayName("올바른 form으로 회원가입 요청이 온다.")
    void userCreateRequest_validForm() {
        final String userId = "id";
        final String email = "email@domain.com";
        final String password = "password^^pa11";
        final String nickname = "nickname";
        // 한글 URI.create
        final String validForm = String.format("userId=%s&email=%s&password=%s&nickname=%s", userId, email, password, nickname);
        final UserCreateRequest expected = new UserCreateRequest(userId, nickname, password, email);

        final UserCreateRequest actual = UserCreateRequest.of(validForm);

        Assertions.assertThat(actual)
                .isEqualTo(expected);
    }
}