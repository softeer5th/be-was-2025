package user;

import handler.request.UserSignUpRequestHandler;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.enums.HttpMethod;
import http.enums.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class SignUpTest {
    private UserSignUpRequestHandler userSignUpRequestHandler = new UserSignUpRequestHandler();

    @Test
    @DisplayName("회원가입 성공")
    void 회원가입_성공(){
        // given
        HttpRequest httpRequest = createSignUpHttpRequest("aaaa", "bbbb", "1234");

        // when
        HttpResponse httpResponse = userSignUpRequestHandler.handle(httpRequest);

        // then
        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.SEE_OTHER);
    }

    @Test
    @DisplayName("회원가입 시 이미 존재하는 사용자 아이디가 있을 경우")
    void 이미_존재하는_사용자_아이다가_있어_회원가입_실패(){
        // given
        HttpRequest httpRequest1 = createSignUpHttpRequest("aaaa", "abcd", "1234");
        userSignUpRequestHandler.handle(httpRequest1);
        HttpRequest httpRequest2 = createSignUpHttpRequest("aaaa", "efgh", "5678");

        // when
        HttpResponse httpResponse = userSignUpRequestHandler.handle(httpRequest2);

        // then
        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("회원가입 시 이미 존재하는 닉네임이 있을 경우")
    void 이미_존재하는_닉네임이_있어_회원가입_실패(){
        // given
        HttpRequest httpRequest1 = createSignUpHttpRequest("aaaa", "abcd", "1234");
        userSignUpRequestHandler.handle(httpRequest1);
        HttpRequest httpRequest2 = createSignUpHttpRequest("bbbb", "abcd", "5678");

        // when
        HttpResponse httpResponse = userSignUpRequestHandler.handle(httpRequest2);

        // then
        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }



    private static HttpRequest createSignUpHttpRequest(String userId, String name, String password) {
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setMethod(HttpMethod.GET.name());
        httpRequest.setPath("/create");

        String bodyString = String.format("userId=%s&name=%s&password=%s", userId, name, password);
        httpRequest.setBody(bodyString.getBytes());
        return httpRequest;
    }
}
