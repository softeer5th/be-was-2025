package handler;

import db.UserDatabase;
import enums.HttpHeader;
import enums.HttpMethod;
import enums.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import request.HttpRequestInfo;
import response.HttpResponse;
import util.SessionManager;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DynamicHomeHandlerTest {
    DynamicHomeHandler dynamicHomeHandler = new DynamicHomeHandler();
    private final UserDatabase userDatabase = UserDatabase.getInstance();
    private final int USER_ID = 21;
    private final static String NICKNAME = "딸기모찌붕어빵";

    @Test
    @DisplayName("로그인한 사용자가 홈화면을 접속할 경우 사용자의 이름이 나타난다.")
    void handle_loginUser() {
        SessionManager sessionManager = SessionManager.getInstance();

        final String sessionId = sessionManager.makeAndSaveSessionId(USER_ID);
        final HttpRequestInfo request = new HttpRequestInfo(HttpMethod.GET, "/index.html", HttpVersion.HTTP1_1, Map.of(HttpHeader.COOKIE.getName(), String.format("SID=%s; Path=/", sessionId)), "");

        final HttpResponse response = dynamicHomeHandler.handle(request);

        final String body = new String(response.getBody(), StandardCharsets.UTF_8);
        System.out.println(body);
        assertThat(body.contains(String.format("%s님", NICKNAME)))
                .isTrue();
    }

    @Test
    @DisplayName("로그인하지 않은 사용자가 홈화면을 접속할 경우 로그인, 회원가입 버튼이 나타난다.")
    void handle_guest() {
        final HttpRequestInfo request = new HttpRequestInfo(HttpMethod.GET, "/index.html", HttpVersion.HTTP1_1, new HashMap<>(), "");

        final HttpResponse response = dynamicHomeHandler.handle(request);

        final String body = new String(response.getBody(), StandardCharsets.UTF_8);
        assertThat(body.contains(String.format("%s님", NICKNAME)))
                .isFalse();
        assertThat(body.contains("로그인"))
                .isTrue();
        assertThat(body.contains("회원 가입"))
                .isTrue();
    }

}