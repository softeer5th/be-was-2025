package http.handler;

import http.request.HttpRequest;
import http.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class BadRequestHandlerTest {

    private BadRequestHandler handler;
    private HttpRequest mockRequest;
    private OutputStream out;

    @BeforeEach
    public void setUp() {
        handler = BadRequestHandler.getInstance();
        mockRequest = mock(HttpRequest.class);
        out = new ByteArrayOutputStream();
    }

    @Test
    @DisplayName("BadRequestHandler가 올바르게 BAD_REQUEST 응답을 처리하는지 테스트")
    public void testHandle() throws IOException {
        HttpResponse mockResponse = handler.handle(mockRequest);

        mockResponse.send(out);
        String responseStatusLine = out.toString().split("\r\n")[0];
        assertEquals("HTTP/1.1 400 Bad Request", responseStatusLine);
    }
}