package webserver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HTTPRequestHeaderTest {
    static HTTPExceptions.Error400 error400;
    static HTTPExceptions.Error404 error404;
    static HTTPExceptions.Error405 error405;
    static HTTPExceptions.Error505 error505;
    static HTTPRequestHeader request;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("정상적인 GET 요청에 대한 처리 테스트")
    public void testGetMethod() {
        String headers = """
                GET /sample HTTP/1.1
                Host: localhost
                Content-Type: text/plain
                Content-Length: 11
                Connection: close
                """;
        byte[] body = "Hello World".getBytes();

        request = new HTTPRequestHeader(headers);
    }

    @Test
    @DisplayName("requestLine의 형식이 잘못된 헤더에 대한 에러 처리 테스트")
    public void testInvalidRequestLine() {
        String headers = """
                POST/sample HTTP/1.1
                Host: localhost
                Content-Type: application/json
                Content-Length: 11
                Connection: close
                """;
        byte[] body = "Hello World".getBytes();

        error400 = assertThrows(HTTPExceptions.Error400.class,
                () -> new HTTPRequestHeader(headers));
        assertEquals(error400.getMessage(), "400 Bad Request: Invalid request line");
    }

    @Test
    @DisplayName("존재하지 않는 HTTP Method에 대한 에러 처리 테스트")
    public void testUnknownMethod() {
        String headers = """
                WRONG /sample HTTP/1.1
                Host: localhost
                Content-Type: application/json
                Content-Length: 11
                Connection: close
                """;
        byte[] body = "Hello World".getBytes();

        error400 = assertThrows(HTTPExceptions.Error400.class,
                () -> new HTTPRequestHeader(headers));
        assertEquals(error400.getMessage(), "400 Bad Request: Invalid HTTP method");
    }

    @Test
    @DisplayName("colon이 없는 헤더에 대한 에러 처리 테스트")
    public void testInvalidColon() {
        String headers = """
                POST /sample HTTP/1.1
                Host: localhost
                Content-Type application/json
                Content-Length: 11
                Connection: close
                """;
        byte[] body = "Hello World".getBytes();

        error400 = assertThrows(HTTPExceptions.Error400.class,
                () -> new HTTPRequestHeader(headers));
        assertEquals(error400.getMessage(), "400 Bad Request: Invalid colon in header");
    }

    @Test
    @DisplayName("필수 헤더 누락에 대한 에러 처리 테스트")
    public void testMissingHeaderKey() {
        String headers = """
                POST /sample HTTP/1.1
                """;
        byte[] body = "Hello World".getBytes();

        error400 = assertThrows(HTTPExceptions.Error400.class,
                () -> new HTTPRequestHeader(headers));
        assertEquals(error400.getMessage(), "400 Bad Request: request header missing key 'Host'");
    }

    @AfterEach
    void tearDown() {
        error400 = null;
        error404 = null;
        error405 = null;
        error505 = null;
        request = null;
    }
}
