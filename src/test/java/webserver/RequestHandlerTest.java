package webserver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.net.Socket;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RequestHandlerTest {
    @Test
    @DisplayName("메인 페이지 요청 처리 테스트")
    void handleRootRequestTest() throws IOException {
        // Arrange
        String request = """
            GET /index.html HTTP/1.1\r
            Host: localhost\r
            User-Agent: TestClient\r
            Accept: text/html\r
            Connection: close\r
            \r
            """;
        InputStream input = new ByteArrayInputStream(request.getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        Socket mockSocket = mock(Socket.class);
        when(mockSocket.getInputStream()).thenReturn(input);
        when(mockSocket.getOutputStream()).thenReturn(output);

        RequestHandler requestHandler = new RequestHandler(mockSocket);

        // Act
        requestHandler.run();

        // Assert
        String response = output.toString();
        assertThat(response).contains("HTTP/1.1 200 OK");
        assertThat(response).contains("<body id=\"index.html\">");
    }

    @Test
    @DisplayName("회원가입 페이지 요청 처리 테스트")
    void handleRegistrationRequestTest() throws IOException {
        // Arrange
        String request = """
            GET /registration HTTP/1.1
            Host: localhost
            User-Agent: TestClient
            Accept: text/html
            Connection: close
            """;
        InputStream input = new ByteArrayInputStream(request.getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        Socket mockSocket = mock(Socket.class);
        when(mockSocket.getInputStream()).thenReturn(input);
        when(mockSocket.getOutputStream()).thenReturn(output);

        RequestHandler requestHandler = new RequestHandler(mockSocket);

        // Act
        requestHandler.run();

        // Assert
        String response = output.toString();
        assertThat(response).contains("HTTP/1.1 200 OK");
        assertThat(response).contains("<body id=\"registration/index.html\">\n");
    }

    @Test
    @DisplayName("회원가입 완료 요청 처리 테스트")
    void handleUserCreateRequestTest() throws IOException {
        // Arrange
        String body = "userId=testId&userName=testName&userPassword=testPassword";
        String request =
                "POST /user/create HTTP/1.1\r\n" +
                        "Host: localhost\r\n" +
                        "Content-Type: application/x-www-form-urlencoded\r\n" +
                        "Content-Length: " + body.getBytes().length + "\r\n" +
                        "\r\n" +
                        body;

        InputStream input = new ByteArrayInputStream(request.getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        Socket mockSocket = mock(Socket.class);
        when(mockSocket.getInputStream()).thenReturn(input);
        when(mockSocket.getOutputStream()).thenReturn(output);

        RequestHandler requestHandler = new RequestHandler(mockSocket);

        // Act
        requestHandler.run();

        // Assert
        String response = output.toString();
        assertThat(response).contains("HTTP/1.1 302 Found");
        assertThat(response).contains("Location: /main/index.html");
    }

    @Test
    @DisplayName("유효하지 않은 요청 처리 테스트")
    void handleInvalidRequestTest() throws IOException {
        // Arrange
        String request = "GET /invalidUrl HTTP/1.1\r\n\r\n";
        InputStream input = new ByteArrayInputStream(request.getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        Socket mockSocket = mock(Socket.class);
        when(mockSocket.getInputStream()).thenReturn(input);
        when(mockSocket.getOutputStream()).thenReturn(output);

        RequestHandler requestHandler = new RequestHandler(mockSocket);

        // Act
        requestHandler.run();

        // Assert
        String response = output.toString();
        assertThat(response).contains("HTTP/1.1 400 Bad Request");
    }

    @Test
    @DisplayName("Content-Length와 body의 길이가 다른 요청 처리 테스트")
    void handleInvalidRequestBodyTest() throws IOException {
        // Assert
        String body = "userId=testId&userName=testName&userPassword=testPassword";
        String request =
                "POST /user/create HTTP/1.1\r\n" +
                        "Host: localhost\r\n" +
                        "Content-Type: application/x-www-form-urlencoded\r\n" +
                        "Content-Length: " + (body.getBytes().length-10) + "\r\n" +
                        "\r\n" +
                        body;
        InputStream input = new ByteArrayInputStream(request.getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        Socket mockSocket = mock(Socket.class);
        when(mockSocket.getInputStream()).thenReturn(input);
        when(mockSocket.getOutputStream()).thenReturn(output);

        RequestHandler requestHandler = new RequestHandler(mockSocket);

        // Act
        requestHandler.run();

        // Assert
        String response = output.toString();
        assertThat(response).contains("body length does not match content-length");
    }
}
