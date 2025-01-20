package handler;

import db.Database;
import enums.HttpHeader;
import enums.HttpMethod;
import enums.HttpStatus;
import enums.HttpVersion;
import model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import request.HttpRequestInfo;
import response.HttpResponse;
import util.SessionManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DynamicFileHandlerTest {

    private final DynamicFileHandler dynamicFileHandler = new DynamicFileHandler();
    private final Database database = Database.getInstance();

    @Test
    @DisplayName("로그인하지 않은 사용자가 마이페이지를 요청하면 로그인화면으로 리다이렉트 된다.")
    void handle_guest() {
        final HttpRequestInfo request = new HttpRequestInfo(HttpMethod.GET, "/mypage/index.html", HttpVersion.HTTP1_1, new HashMap<>(), "");

        final HttpResponse response = dynamicFileHandler.handle(request);

        Assertions.assertThat(response.getStatus())
                .isEqualTo(HttpStatus.FOUND);
        Assertions.assertThat(response.getHeaderValue(HttpHeader.LOCATION.getName()))
                .isEqualTo("/login/index.html");
    }

    private static final String STATIC_MYPAGE_HTML = "src/test/resources/static/mypage/index.html";

    @Test
    @DisplayName("로그인한 사용자가 마이페이지를 요청하면 마이페이지 정적 파일을 응답한다.")
    void handle_loginUser() throws IOException {
        SessionManager sessionManager = SessionManager.getInstance();
        database.addUser(new User("test", "test", "test", "test"));
        final String sessionId = sessionManager.makeAndSaveSessionId("test");
        final HttpRequestInfo request = new HttpRequestInfo(HttpMethod.GET, "/mypage/index.html", HttpVersion.HTTP1_1, Map.of(HttpHeader.COOKIE.getName(), String.format("SID=%s; Path=/", sessionId)), "");
        byte[] expected = Files.readAllBytes(new File(STATIC_MYPAGE_HTML).toPath());

        final HttpResponse response = dynamicFileHandler.handle(request);

        assertThat(response.getBody())
                .isEqualTo(expected);
    }

}