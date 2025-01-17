package webserver;

import fixtureUtil.ExceptionRouter;
import fixtureUtil.TestRouter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;
import java.net.Socket;

import static org.mockito.Mockito.when;

class RequestDispatcherTest {

    @Test
    @DisplayName("핸들러에서 정상적인 응답을 반환하면 요청을 클라이언트에 전송한다.")
    void run() throws IOException {
        // given
        Socket connection = Mockito.mock(Socket.class);
        RequestDispatcher requestDispatcher = new RequestDispatcher(connection, new TestRouter());

        String httpRequest = """
                POST /test HTTP/1.1\r
                Accept: application/json\r
                Accept-Encoding: gzip, deflate\r
                Connection: keep-alive\r
                Content-Length: 4\r
                Content-Type: application/json\r
                Host: google.com\r
                User-Agent: HTTPie/0.9.3\r
                \r
                gigi\r
                """;

        byte[] httpRequestBytes = httpRequest.getBytes();

        InputStream mockInputStream = new ByteArrayInputStream(httpRequestBytes);
        OutputStream mockOutputStream = new ByteArrayOutputStream();

        when(connection.getInputStream())
                .thenReturn(mockInputStream);
        when(connection.getOutputStream())
                .thenReturn(mockOutputStream);

        final String expected = """
                HTTP/1.1 200 Ok \r
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


        String httpRequest = """
                POST /test HTTP/1.1\r
                Accept: application/json\r
                Accept-Encoding: gzip, deflate\r
                Connection: keep-alive\r
                Content-Length: 4\r
                Content-Type: application/json\r
                Host: google.com\r
                User-Agent: HTTPie/0.9.3\r
                \r
                gigi\r
                """;

        byte[] httpRequestBytes = httpRequest.getBytes();

        InputStream mockInputStream = new ByteArrayInputStream(httpRequestBytes);
        OutputStream mockOutputStream = new ByteArrayOutputStream();

        when(connection.getInputStream())
                .thenReturn(mockInputStream);
        when(connection.getOutputStream())
                .thenReturn(mockOutputStream);

        final String expected = """
                HTTP/1.1 400 Bad Request \r
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