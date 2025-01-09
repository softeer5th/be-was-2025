package webserver;

import fixtureUtil.TestRouter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import static org.mockito.Mockito.when;

class RequestDispatcherTest {

    @Test
    @DisplayName("핸들러에서 정상적인 응답을 반환하면 요청을 클라이언트에 전송한다.")
    void run() throws IOException {
        // given
        Socket connection = Mockito.mock(Socket.class);
        RequestDispatcher requestDispatcher = new RequestDispatcher(connection, new TestRouter());

        byte[] httpRequest = Files.readAllBytes(new File("src/test/java/fixtureUtil/HttpRequestFixture").toPath());

        InputStream mockInputStream = new ByteArrayInputStream(httpRequest);
        OutputStream mockOutputStream = new ByteArrayOutputStream();

        when(connection.getInputStream())
                .thenReturn(mockInputStream);
        when(connection.getOutputStream())
                .thenReturn(mockOutputStream);

        final String expected = """
                HTTP/1.1 200 OK \r
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
}