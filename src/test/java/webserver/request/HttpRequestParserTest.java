package webserver.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.enums.HttpMethod;
import webserver.enums.HttpStatusCode;
import webserver.enums.ParsingConstant;
import webserver.exception.BadRequest;
import webserver.exception.HttpException;
import webserver.exception.NotImplemented;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpRequestParserTest {

    @Test
    @DisplayName("GET Request Parsing")
    void parseTest1() throws IOException {
        // given
        var requestString = String.join("\r\n",
                "GET /index.html?page=1&pageSize=10&filter= HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/html",
                "\r\n");

        try (var in = new ByteArrayInputStream(requestString.getBytes())) {

            var parser = new HttpRequestParser();
            // when
            var request = parser.parse(in);
            // then
            assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
            assertThat(request.getRequestTarget().getPath()).isEqualTo("/index.html");
            assertThat(request.getRequestTarget().getQuery())
                    .containsEntry("page", "1")
                    .containsEntry("pageSize", "10")
                    .containsEntry("filter", "");
            assertThat(request.getVersion().version).isEqualTo("HTTP/1.1");
            assertThat(request.getHeaders().getHeader("Host")).isEqualTo("localhost:8080");
            assertThat(request.getHeaders().getHeader("Accept")).isEqualTo("text/html");

        }
    }

    @Test
    @DisplayName("Post Request Parsing")
    void parseTest2() throws IOException {
        // given
        var bodyString = """
                id=id1
                password=password1""";
        var requestString = String.join("\r\n",
                "POST /users HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: 25",
                "Content-Type: text/plain",
                "",
                bodyString);
        try (var in = new ByteArrayInputStream(requestString.getBytes())) {
            var parser = new HttpRequestParser();
            // when
            var request = parser.parse(in);
            // then
            assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
            assertThat(request.getRequestTarget().getPath()).isEqualTo("/users");
            assertThat(request.getVersion().version).isEqualTo("HTTP/1.1");
            assertThat(request.getHeaders().getHeader("Host")).isEqualTo("localhost:8080");
            assertThat(request.getHeaders().getHeader("Content-Length")).isEqualTo("25");
            assertThat(request.getHeaders().getHeader("Content-Type")).isEqualTo("text/plain");
            assertThat(request.readBodyAsString()).isEqualTo("""
                    id=id1
                    password=password1""");
        }
    }

    @Test
    @DisplayName("잘못된 Request Line separator")
    void parseTest3() throws IOException {
        // given
        var requestString = String.join("\r\n",
                "GET  /users HTTP/1.1",
                "Host: localhost:8080",
                "\r\n");
        try (var in = new ByteArrayInputStream(requestString.getBytes())) {
            var parser = new HttpRequestParser();
            // when
            // then
            assertThatThrownBy(() -> parser.parse(in))
                    .isInstanceOf(BadRequest.class);
        }
    }

    @Test
    @DisplayName("잘못된 Method")
    void parseTest4() throws IOException {
        // given

        var requestString = String.join("\r\n",
                "GAT /users HTTP/1.1",
                "Host: localhost:8080",
                "\r\n");
        try (var in = new ByteArrayInputStream(requestString.getBytes())) {
            var parser = new HttpRequestParser();
            // when
            // then
            assertThatThrownBy(() -> parser.parse(in))
                    .isInstanceOf(NotImplemented.class);
        }
    }

    @Test
    @DisplayName("잘못된 Query")
    void parseTest5() throws IOException {
        // given

        var requestString = String.join("\r\n",
                "GET /users?id=a&password HTTP/1.1",
                "Host: localhost:8080",
                "\r\n");
        try (var in = new ByteArrayInputStream(requestString.getBytes())) {
            var parser = new HttpRequestParser();
            // when
            // then
            assertThatThrownBy(() -> parser.parse(in))
                    .isInstanceOf(BadRequest.class);
        }
    }

    @Test
    @DisplayName("Header name이 대소문자 섞여있을 때 정상작동 해야 함")
    void parseTest6() throws IOException {
        // given
        var requestString = String.join("\r\n",
                "GET /index.html?page=1&pageSize=10&filter= HTTP/1.1",
                "Host: localhost:8080",
                "aCcEpT: text/html",
                "\r\n");
        try (var in = new ByteArrayInputStream(requestString.getBytes())) {
            var parser = new HttpRequestParser();
            // when
            var request = parser.parse(in);
            // then
            assertThat(request.getHeaders().getHeader("Host")).isEqualTo("localhost:8080");
            assertThat(request.getHeaders().getHeader("Accept")).isEqualTo("text/html");
        }
    }

    @Test
    @DisplayName("request line 앞에 연속된 CRLF가 있는 경우 무시해야 함")
    void parseTest7() throws IOException {
        // given
        var requestString = String.join("\r\n",
                "",
                "",
                "",
                "GET /index.html?page=1&pageSize=10&filter= HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/html",
                "\r\n");
        try (var in = new ByteArrayInputStream(requestString.getBytes())) {
            var parser = new HttpRequestParser();
            // when
            var request = parser.parse(in);
            // then
            assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
            assertThat(request.getRequestTarget().getPath()).isEqualTo("/index.html");
            assertThat(request.getRequestTarget().getQuery())
                    .containsEntry("page", "1")
                    .containsEntry("pageSize", "10")
                    .containsEntry("filter", "");
            assertThat(request.getVersion().version).isEqualTo("HTTP/1.1");
            assertThat(request.getHeaders().getHeader("Host")).isEqualTo("localhost:8080");
            assertThat(request.getHeaders().getHeader("Accept")).isEqualTo("text/html");
        }
    }

    @Test
    @DisplayName("request line 앞에 연속된 CRLF와 LF가 있는 경우 무시해야 함")
    void parseTest8() throws IOException {
        // given
        var requestString =
                "\n\n\r\n\n\r\n\r\n" +
                        String.join("\r\n",
                                "GET /index.html?page=1&pageSize=10&filter= HTTP/1.1",
                                "Host: localhost:8080",
                                "Accept: text/html",
                                "\r\n");
        try (var in = new ByteArrayInputStream(requestString.getBytes())) {
            var parser = new HttpRequestParser();
            // when
            var request = parser.parse(in);
            // then
            assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
            assertThat(request.getRequestTarget().getPath()).isEqualTo("/index.html");
            assertThat(request.getRequestTarget().getQuery())
                    .containsEntry("page", "1")
                    .containsEntry("pageSize", "10")
                    .containsEntry("filter", "");
            assertThat(request.getVersion().version).isEqualTo("HTTP/1.1");
            assertThat(request.getHeaders().getHeader("Host")).isEqualTo("localhost:8080");
            assertThat(request.getHeaders().getHeader("Accept")).isEqualTo("text/html");
        }
    }

    @Test
    @DisplayName("request line 앞에 LF가 있는 경우 오류가 발생해야 함")
    void parseTest9() throws IOException {
        // given
        var requestString = "\r" +
                String.join("\r\n",
                        "GET /users HTTP/1.1",
                        "Host: localhost:8080",
                        "\r\n");
        try (var in = new ByteArrayInputStream(requestString.getBytes())) {
            var parser = new HttpRequestParser();
            // when
            // then
            assertThatThrownBy(() -> parser.parse(in))
                    .isInstanceOf(BadRequest.class);
        }
    }

    @Test
    @DisplayName("header가 너무 크면 431 오류가 발생해야 함")
    void parseTest10() throws IOException {
        // given
        var requestString = "\r" +
                String.join("\r\n",
                        "GET /users HTTP/1.1",
                        "Host: localhost:8080",
                        "Accept: " + "a".repeat(ParsingConstant.MAX_HEADER_SIZE),
                        "\r\n");
        try (var in = new ByteArrayInputStream(requestString.getBytes())) {
            var parser = new HttpRequestParser();
            // when
            // then
            assertThatThrownBy(() -> parser.parse(in))
                    .isInstanceOf(HttpException.class)
                    .matches(e -> ((HttpException) e).getStatusCode() == HttpStatusCode.REQUEST_HEADER_FIELDS_TOO_LARGE.statusCode);
        }
    }


    @Test
    @DisplayName("header name에 공백이 있는 경우 오류가 발생해야 함")
    void parseTest11() throws IOException {
        // given
        var bodyString = "id=admin&name=sara&email=sara@test.com";
        var requestString = String.join("\r\n",
                "PUT /users/admin HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length : 38",
                "Content-Type: text/plain",
                "",
                bodyString);
        try (var in = new ByteArrayInputStream(requestString.getBytes())) {
            var parser = new HttpRequestParser();
            // when
            // then
            assertThatThrownBy(() -> parser.parse(in))
                    .isInstanceOf(BadRequest.class);
        }
    }

}