package http.handler;

import http.enums.ErrorMessage;
import http.enums.HttpResponseStatus;
import http.request.HttpRequest;
import http.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class BadRequestHandlerTest {

    private BadRequestHandler handler;
    private HttpRequest mockRequest;
    private HttpResponse mockResponse;

    @BeforeEach
    public void setUp() {
        handler = BadRequestHandler.getInstance();
        mockRequest = mock(HttpRequest.class);
        mockResponse = mock(HttpResponse.class);
    }

    @Test
    @DisplayName("BadRequestHandler가 올바르게 BAD_REQUEST 응답을 처리하는지 테스트")
    public void testHandle() throws IOException {
        handler.handle(mockRequest, mockResponse);

        verify(mockResponse).sendErrorResponse(HttpResponseStatus.BAD_REQUEST, ErrorMessage.BAD_REQUEST);
        verifyNoMoreInteractions(mockResponse);
    }
}