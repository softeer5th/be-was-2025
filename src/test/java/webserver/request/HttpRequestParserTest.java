package webserver.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.enums.HttpMethod;
import webserver.exception.BadRequest;
import webserver.exception.NotImplemented;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

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
                "");
        var reader = new BufferedReader(new StringReader(requestString));
        var parser = new HttpRequestParser();
        // when
        var request = parser.parse(reader);
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
        reader.close();
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
        var reader = new BufferedReader(new StringReader(requestString));
        var parser = new HttpRequestParser();
        // when
        var request = parser.parse(reader);
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
        reader.close();
    }

    @Test
    @DisplayName("잘못된 Request Line separator")
    void parseTest3() throws IOException {
        // given

        var requestString = String.join("\r\n",
                "GET  /users HTTP/1.1",
                "Host: localhost:8080",
                "");
        var reader = new BufferedReader(new StringReader(requestString));
        var parser = new HttpRequestParser();
        // when
        // then
        assertThatThrownBy(() -> parser.parse(reader))
                .isInstanceOf(BadRequest.class);
        reader.close();
    }

    @Test
    @DisplayName("잘못된 Method")
    void parseTest4() throws IOException {
        // given

        var requestString = String.join("\r\n",
                "GAT /users HTTP/1.1",
                "Host: localhost:8080",
                "");
        var reader = new BufferedReader(new StringReader(requestString));
        var parser = new HttpRequestParser();
        // when
        // then
        assertThatThrownBy(() -> parser.parse(reader))
                .isInstanceOf(NotImplemented.class);
        reader.close();
    }

    @Test
    @DisplayName("잘못된 Query")
    void parseTest5() throws IOException {
        // given

        var requestString = String.join("\r\n",
                "GET /users?id=a&password HTTP/1.1",
                "Host: localhost:8080",
                "");
        var reader = new BufferedReader(new StringReader(requestString));
        var parser = new HttpRequestParser();
        // when
        // then
        assertThatThrownBy(() -> parser.parse(reader))
                .isInstanceOf(BadRequest.class);
        reader.close();
    }

    @Test
    @DisplayName("Header name이 대소문자 섞여있을 때")
    void parseTest6() throws IOException {
        // given
        var requestString = String.join("\r\n",
                "GET /index.html?page=1&pageSize=10&filter= HTTP/1.1",
                "Host: localhost:8080",
                "aCcEpT: text/html",
                "");
        var reader = new BufferedReader(new StringReader(requestString));
        var parser = new HttpRequestParser();
        // when
        var request = parser.parse(reader);
        // then
        assertThat(request.getHeaders().getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(request.getHeaders().getHeader("Accept")).isEqualTo("text/html");
        reader.close();
    }
}