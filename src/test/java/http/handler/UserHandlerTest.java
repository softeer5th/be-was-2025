package http.handler;

import http.enums.HttpMethod;
import http.request.HttpRequest;
import http.request.TargetInfo;
import http.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserHandlerTest {

    private UserHandler handler;
    private HttpRequest mockRequest;
    private HttpResponse mockResponse;
    private OutputStream out;

    @BeforeEach
    public void setUp() {
        handler = UserHandler.getInstance();
        mockRequest = mock(HttpRequest.class);
        out = new ByteArrayOutputStream();
    }

    @Test
    @DisplayName("회원가입 요청 성공 테스트")
    public void testHandleUserCreateSuccess() throws IOException, URISyntaxException {
        when(mockRequest.getMethod()).thenReturn(HttpMethod.POST);
        when(mockRequest.getTarget()).thenReturn(new TargetInfo("/user/create"));

        when(mockRequest.getBody()).thenReturn("userId=testUser&name=Test User&password=password123&email=test@example.com");

        mockResponse = handler.handle(mockRequest);

        mockResponse.send(out);
        String responseStatusLine = out.toString().split("\r\n")[0];
        assertEquals("HTTP/1.1 302 Found", responseStatusLine);
    }

    @Test
    @DisplayName("회원가입 요청 실패 테스트 - 필드 누락")
    public void testHandleUserCreateMissingFields() throws IOException, URISyntaxException {
        when(mockRequest.getMethod()).thenReturn(HttpMethod.POST);
        when(mockRequest.getTarget()).thenReturn(new TargetInfo("/user/create"));

        when(mockRequest.getBody()).thenReturn("userId=testUser&name=Test User&password=password123");

        mockResponse = handler.handle(mockRequest);

        mockResponse.send(out);
        String responseStatusLine = out.toString().split("\r\n")[0];
        assertEquals("HTTP/1.1 400 Bad Request", responseStatusLine);
    }

    @Test
    @DisplayName("존재하지 않는 경로 요청 처리 테스트")
    public void testHandleNotFoundPath() throws IOException, URISyntaxException {
        when(mockRequest.getMethod()).thenReturn(HttpMethod.GET);
        when(mockRequest.getTarget()).thenReturn(new TargetInfo("/user/unknown"));

        mockResponse = handler.handle(mockRequest);

        mockResponse.send(out);
        String responseStatusLine = out.toString().split("\r\n")[0];
        assertEquals("HTTP/1.1 404 Not Found", responseStatusLine);
    }
}