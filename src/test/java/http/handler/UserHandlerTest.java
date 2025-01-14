package http.handler;

import http.enums.HttpMethod;
import http.enums.HttpResponseStatus;
import http.request.HttpRequest;
import http.request.TargetInfo;
import http.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class UserHandlerTest {

    private UserHandler handler;
    private HttpRequest mockRequest;
    private HttpResponse mockResponse;

    @BeforeEach
    public void setUp() {
        handler = UserHandler.getInstance();
        mockRequest = mock(HttpRequest.class);
        mockResponse = mock(HttpResponse.class);
    }

    @Test
    @DisplayName("회원가입 요청 성공 테스트")
    public void testHandleUserCreateSuccess() throws IOException, URISyntaxException {
        when(mockRequest.getMethod()).thenReturn(HttpMethod.POST);
        when(mockRequest.getTarget()).thenReturn(new TargetInfo("/user/create"));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userId", "testUser");
        requestBody.put("name", "Test User");
        requestBody.put("password", "password123");
        requestBody.put("email", "test@example.com");
        when(mockRequest.getBody()).thenReturn("userId=testUser&name=Test User&password=password123&email=test@example.com");

        handler.handle(mockRequest, mockResponse);

        verify(mockResponse).sendRedirectResponse(HttpResponseStatus.FOUND, "/index.html");
    }

    @Test
    @DisplayName("회원가입 요청 실패 테스트 - 필드 누락")
    public void testHandleUserCreateMissingFields() throws IOException, URISyntaxException {
        when(mockRequest.getMethod()).thenReturn(HttpMethod.POST);
        when(mockRequest.getTarget()).thenReturn(new TargetInfo("/user/create"));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userId", "testUser");
        requestBody.put("name", "Test User");
        requestBody.put("password", "password123");
        when(mockRequest.getBody()).thenReturn("userId=testUser&name=Test User&password=password123");

        handler.handle(mockRequest, mockResponse);

        verify(mockResponse).sendErrorResponse(HttpResponseStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("존재하지 않는 경로 요청 처리 테스트")
    public void testHandleNotFoundPath() throws IOException, URISyntaxException {
        when(mockRequest.getMethod()).thenReturn(HttpMethod.GET);
        when(mockRequest.getTarget()).thenReturn(new TargetInfo("/user/unknown"));

        handler.handle(mockRequest, mockResponse);

        verify(mockResponse).sendErrorResponse(HttpResponseStatus.NOT_FOUND);
    }
}