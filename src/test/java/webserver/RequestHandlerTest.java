package webserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.net.Socket;

import static org.mockito.Mockito.*;

class RequestHandlerTest {

    @Mock
    private Socket mockSocket;

    @Mock
    private InputStream mockInputStream;

    @Mock
    private OutputStream mockOutputStream;

    private RequestHandler requestHandler;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        when(mockSocket.getInputStream()).thenReturn(mockInputStream);
        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);
        requestHandler = new RequestHandler(mockSocket);
    }

    @Test
    @DisplayName("유효한 HTTP 요청 처리 테스트")
    void testRunWithValidRequest() throws IOException {
        String httpRequest = "GET /index.html HTTP/1.1\r\n\r\n";
        ByteArrayInputStream bais = new ByteArrayInputStream(httpRequest.getBytes());
        when(mockSocket.getInputStream()).thenReturn(bais);

        requestHandler.run();

        verify(mockSocket).getInputStream();
        verify(mockSocket).getOutputStream();
    }

    @Test
    @DisplayName("잘못된 HTTP 요청 처리 테스트")
    void testRunWithInvalidRequest() throws IOException {
        String invalidRequest = "INVALID REQUEST\r\n\r\n";
        ByteArrayInputStream bais = new ByteArrayInputStream(invalidRequest.getBytes());
        when(mockSocket.getInputStream()).thenReturn(bais);

        requestHandler.run();

        verify(mockSocket).getInputStream();
    }
}