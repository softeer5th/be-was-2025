package http.handler;

import db.Database;
import db.SessionDB;
import http.enums.HttpMethod;
import http.request.HttpRequest;
import http.request.TargetInfo;
import http.response.HttpResponse;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.JwtUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserHandlerTest {

    private UserHandler handler;
    private HttpRequest mockRequest;
    private ByteArrayOutputStream out;

    @BeforeEach
    void setUp() {
        handler = UserHandler.getInstance();
        mockRequest = mock(HttpRequest.class);
        out = new ByteArrayOutputStream();

        // 테스트용 유저 세팅
        // 이미 존재하는 유저: userId="testUser"
        Database.addUser(new User("testUser", "password123", "Test User", "test@example.com"));
    }

    @Test
    @DisplayName("POST /user/create - 회원가입 성공")
    void testHandleUserCreateSuccess() throws IOException, URISyntaxException {
        when(mockRequest.getMethod()).thenReturn(HttpMethod.POST);
        when(mockRequest.getTarget()).thenReturn(new TargetInfo("/user/create"));

        // 모든 파라미터 정상
        when(mockRequest.getBody()).thenReturn("userId=newUser&name=New User&password=newPw123&email=new@example.com");

        HttpResponse response = handler.handle(mockRequest);
        response.send(out);

        String[] lines = out.toString().split("\r\n");
        String statusLine = lines[0];
        assertEquals("HTTP/1.1 302 Found", statusLine);
    }

    @Test
    @DisplayName("POST /user/create - 필드 누락으로 인한 BAD_REQUEST")
    void testHandleUserCreateMissingFields() throws IOException, URISyntaxException {
        when(mockRequest.getMethod()).thenReturn(HttpMethod.POST);
        when(mockRequest.getTarget()).thenReturn(new TargetInfo("/user/create"));

        // email 누락
        when(mockRequest.getBody()).thenReturn("userId=noEmail&name=NoEmailUser&password=pass123");

        HttpResponse response = handler.handle(mockRequest);
        response.send(out);

        String[] lines = out.toString().split("\r\n");
        String statusLine = lines[0];
        assertEquals("HTTP/1.1 400 Bad Request", statusLine);
    }

    @Test
    @DisplayName("POST /user/create - 이미 존재하는 userId로 인한 BAD_REQUEST")
    void testHandleUserCreateDuplicate() throws IOException, URISyntaxException {
        when(mockRequest.getMethod()).thenReturn(HttpMethod.POST);
        when(mockRequest.getTarget()).thenReturn(new TargetInfo("/user/create"));

        // 이미 DB.addUser된 "testUser"
        when(mockRequest.getBody()).thenReturn("userId=testUser&name=DupName&password=dup123&email=dup@example.com");

        HttpResponse response = handler.handle(mockRequest);
        response.send(out);

        String[] lines = out.toString().split("\r\n");
        String statusLine = lines[0];
        assertEquals("HTTP/1.1 400 Bad Request", statusLine);
    }

    @Test
    @DisplayName("POST /user/login - 로그인 성공")
    void testHandleUserLoginSuccess() throws IOException, URISyntaxException {
        when(mockRequest.getMethod()).thenReturn(HttpMethod.POST);
        when(mockRequest.getTarget()).thenReturn(new TargetInfo("/user/login"));

        // DB에 있는 userId=testUser, password=password123
        when(mockRequest.getBody()).thenReturn("userId=testUser&password=password123");

        HttpResponse response = handler.handle(mockRequest);
        response.send(out);

        String result = out.toString();
        String[] lines = result.split("\r\n");
        String statusLine = lines[0];
        assertEquals("HTTP/1.1 302 Found", statusLine);

        // Set-Cookie: sid=... 포함 여부 확인
        // 간단 검증
        boolean hasSetCookie = result.contains("Set-Cookie: sid=");
        assertEquals(true, hasSetCookie);
    }

    @Test
    @DisplayName("POST /user/login - 사용자 미존재 or 잘못된 비밀번호 -> UNAUTHORIZED")
    void testHandleUserLoginFail() throws IOException, URISyntaxException {
        when(mockRequest.getMethod()).thenReturn(HttpMethod.POST);
        when(mockRequest.getTarget()).thenReturn(new TargetInfo("/user/login"));

        // 패스워드 틀림
        when(mockRequest.getBody()).thenReturn("userId=testUser&password=wrongPw");

        HttpResponse response = handler.handle(mockRequest);
        response.send(out);

        String[] lines = out.toString().split("\r\n");
        String statusLine = lines[0];
        assertEquals("HTTP/1.1 401 Unauthorized", statusLine);
    }

    @Test
    @DisplayName("POST /user/logout - 로그아웃 시 쿠키 만료 및 세션 제거, 302 Found")
    void testHandleUserLogout() throws IOException, URISyntaxException {
        when(mockRequest.getMethod()).thenReturn(HttpMethod.POST);
        when(mockRequest.getTarget()).thenReturn(new TargetInfo("/user/logout"));

        User user = Database.findUserById("testUser");
        String sid = JwtUtil.generateToken(user);
        SessionDB.saveSession(sid, user);
        when(mockRequest.getHeaders()).thenReturn(Map.of(
        "Cookie", "sid="+sid
        ));

        HttpResponse response = handler.handle(mockRequest);
        response.send(out);

        String result = out.toString();
        String[] lines = result.split("\r\n");
        String statusLine = lines[0];
        assertEquals("HTTP/1.1 302 Found", statusLine);
    }

    @Test
    @DisplayName("존재하지 않는 경로인 경우 404 Not Found")
    void testHandleUnknownPath() throws IOException, URISyntaxException {
        when(mockRequest.getMethod()).thenReturn(HttpMethod.GET);
        when(mockRequest.getTarget()).thenReturn(new TargetInfo("/user/unknown"));

        HttpResponse response = handler.handle(mockRequest);
        response.send(out);

        String[] lines = out.toString().split("\r\n");
        assertEquals("HTTP/1.1 404 Not Found", lines[0]);
    }
}