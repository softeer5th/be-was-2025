package webserver;

import org.junit.jupiter.api.BeforeEach;
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
    void testRunWithValidRequest() throws IOException {
        // 테스트용 HTTP 요청 준비
        String httpRequest = "GET /index.html HTTP/1.1\r\n\r\n";
        ByteArrayInputStream bais = new ByteArrayInputStream(httpRequest.getBytes());
        when(mockSocket.getInputStream()).thenReturn(bais);

        // 실행
        requestHandler.run();

        // 검증
        verify(mockSocket).getInputStream();
        verify(mockSocket).getOutputStream();
        // 추가적인 검증 로직 (예: 응답 헤더, 본문 등)
    }

    @Test
    void testRunWithInvalidRequest() throws IOException {
        // 잘못된 요청 준비
        String invalidRequest = "INVALID REQUEST\r\n\r\n";
        ByteArrayInputStream bais = new ByteArrayInputStream(invalidRequest.getBytes());
        when(mockSocket.getInputStream()).thenReturn(bais);

        // 실행
        requestHandler.run();

        // 검증
        verify(mockSocket).getInputStream();
        // 추가적인 검증 로직 (예: 에러 응답 확인)
    }
}