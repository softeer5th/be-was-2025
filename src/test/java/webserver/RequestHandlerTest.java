package webserver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.net.Socket;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RequestHandlerTest {
    private Socket mockSocket;
    private ByteArrayOutputStream output;
    private ByteArrayInputStream input;

    @BeforeEach
    void setUp() throws IOException {
        mockSocket = mock(Socket.class);
        output = new ByteArrayOutputStream();
        when(mockSocket.getOutputStream()).thenReturn(output);
    }

    @AfterEach
    void tearDown() throws IOException {
        input.close();
        output.close();
        mockSocket.close();
    }

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
        input = new ByteArrayInputStream(request.getBytes());

        when(mockSocket.getInputStream()).thenReturn(input);

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
        input = new ByteArrayInputStream(request.getBytes());

        when(mockSocket.getInputStream()).thenReturn(input);

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
        String body = "id=testId&name=testName&password=testPassword";
        String request =
                "POST /user/create HTTP/1.1\r\n" +
                        "Host: localhost\r\n" +
                        "Content-Type: application/x-www-form-urlencoded\r\n" +
                        "Content-Length: " + body.getBytes().length + "\r\n" +
                        "\r\n" +
                        body;

        input = new ByteArrayInputStream(request.getBytes());

        when(mockSocket.getInputStream()).thenReturn(input);

        RequestHandler requestHandler = new RequestHandler(mockSocket);

        // Act
        requestHandler.run();

        // Assert
        String response = output.toString();
        assertThat(response).contains("HTTP/1.1 302 Found");
        assertThat(response).contains("Location: /index.html");
    }

    @Test
    @DisplayName("유효하지 않은 요청 처리 테스트")
    void handleInvalidRequestTest() throws IOException {
        // Arrange
        String request = "GET /invalidUrl HTTP/1.1\r\n\r\n";
        input = new ByteArrayInputStream(request.getBytes());

        when(mockSocket.getInputStream()).thenReturn(input);

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
        input = new ByteArrayInputStream(request.getBytes());

        when(mockSocket.getInputStream()).thenReturn(input);

        RequestHandler requestHandler = new RequestHandler(mockSocket);

        // Act
        requestHandler.run();

        // Assert
        String response = output.toString();
        assertThat(response).contains("Content-Length header mismatch");
    }
}
