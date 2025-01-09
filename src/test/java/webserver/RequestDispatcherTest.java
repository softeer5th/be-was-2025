package webserver;

import fixtureUtil.ExceptionRouter;
import fixtureUtil.TestRouter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import static org.mockito.Mockito.when;

class RequestDispatcherTest {
    private byte[] HTTP_REQUEST_SAMPLE;

    @BeforeEach
    void setHttpRequestSample() throws IOException {
        HTTP_REQUEST_SAMPLE = Files.readAllBytes(new File("src/test/java/fixtureUtil/HttpRequestSample").toPath());
    }

    @Test
    @DisplayName("핸들러에서 정상적인 응답을 반환하면 요청을 클라이언트에 전송한다.")
    void run() throws IOException {
        // given
        Socket connection = Mockito.mock(Socket.class);
        RequestDispatcher requestDispatcher = new RequestDispatcher(connection, new TestRouter());

        byte[] httpRequest = HTTP_REQUEST_SAMPLE;

        InputStream mockInputStream = new ByteArrayInputStream(httpRequest);
        OutputStream mockOutputStream = new ByteArrayOutputStream();

        when(connection.getInputStream())
                .thenReturn(mockInputStream);
        when(connection.getOutputStream())
                .thenReturn(mockOutputStream);

        final String expected = """
                HTTP/1.1 200 ok \r
                Content-Length: 10\r
                Content-Type: text/html; charset=utf-8\r
                \r
                test pass!\r
                """;

        // when
        requestDispatcher.run();

        // then
        Assertions.assertThat(mockOutputStream.toString())
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("핸들러에서 client exception을 던지면 4xx 응답을 클라이언트에 전송한다.")
    void run_clientException() throws IOException {
        // given
        Socket connection = Mockito.mock(Socket.class);
        RequestDispatcher requestDispatcher = new RequestDispatcher(connection, new ExceptionRouter());


        byte[] httpRequest = HTTP_REQUEST_SAMPLE;

        InputStream mockInputStream = new ByteArrayInputStream(httpRequest);
        OutputStream mockOutputStream = new ByteArrayOutputStream();

        when(connection.getInputStream())
                .thenReturn(mockInputStream);
        when(connection.getOutputStream())
                .thenReturn(mockOutputStream);

        final String expected = """
                HTTP/1.1 400 bad request \r
                Content-Length: 31\r
                Content-Type: text/html; charset=utf-8\r
                \r
                잘못된 HTTP 요청입니다.\r
                """;

        // when
        requestDispatcher.run();

        // then
        Assertions.assertThat(mockOutputStream.toString())
                .isEqualTo(expected);
    }
}