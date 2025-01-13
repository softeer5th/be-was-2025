package request;

import exception.ClientErrorException;
import exception.ErrorCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserCreateRequestTest {
    @Test
    @DisplayName("잘못된 form으로 회원가입 요청이 올 경우 에러가 발생한다.")
    void userCreateRequest_invalidForm() {
        final String invalidForm = "user=jueun&myname=jueun&email=hi";

        Assertions.assertThatThrownBy(() -> UserCreateRequest.of(invalidForm))
                .isInstanceOf(ClientErrorException.class)
                .hasMessage(ErrorCode.INVALID_FORM.getMessage());
    }

    @Test
    @DisplayName("올바른 form으로 회원가입 요청이 온다.")
    void userCreateRequest_validForm() {
        final String userId = "id";
        final String email = "email";
        final String password = "password";
        final String nickname = "nickname";
        // 한글 URI.create
        final String validForm = String.format("userId=%s&email=%s&password=%s&nickname=%s", userId, email, password, nickname);
        final UserCreateRequest expected = new UserCreateRequest(userId, nickname, password, email);

        final UserCreateRequest actual = UserCreateRequest.of(validForm);

        Assertions.assertThat(actual)
                .isEqualTo(expected);
    }
}