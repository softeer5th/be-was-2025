package webserver.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.enums.HttpStatusCode;
import webserver.enums.HttpVersion;
import webserver.header.SetCookie;
import webserver.request.HttpRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HttpResponseWriterTest {
    @Test
    @DisplayName("응답 상태 코드와 메시지 작성")
    void writeResponseStatusAndMessage() throws IOException {
        // given
        var request = mock(HttpRequest.class);
        when(request.getVersion()).thenReturn(HttpVersion.HTTP_1_1);
        var response = new HttpResponse(HttpStatusCode.OK);
        response.setBody("OK");
        var out = new ByteArrayOutputStream();

        // when
        new HttpResponseWriter().writeResponse(request, response, out);

        // then
        var result = out.toString();
        assertThat(result).contains("HTTP/1.1 200 OK");
        assertThat(result).contains("Content-Length: 2");
        assertThat(result).contains("OK");
    }

    @Test
    @DisplayName("Set-Cookie 헤더 작성")
    void writeSetCookieHeader() throws IOException {
        // given
        var request = mock(HttpRequest.class);
        when(request.getVersion()).thenReturn(HttpVersion.HTTP_1_1);
        var response = new HttpResponse(HttpStatusCode.OK);
        response.getHeaders().addSetCookie(new SetCookie("sessionId", "abc123"));
        var out = new ByteArrayOutputStream();

        // when
        new HttpResponseWriter().writeResponse(request, response, out);

        // then
        var result = out.toString();
        assertThat(result).contains("Set-Cookie: sessionId=abc123");
    }

    @Test
    @DisplayName("빈 Body 작성")
    void writeEmptyBody() throws IOException {
        // given
        var request = mock(HttpRequest.class);
        when(request.getVersion()).thenReturn(HttpVersion.HTTP_1_1);
        var response = new HttpResponse(HttpStatusCode.NO_CONTENT);
        var out = new ByteArrayOutputStream();

        // when
        new HttpResponseWriter().writeResponse(request, response, out);

        // then
        var result = out.toString();
        assertThat(result).contains("HTTP/1.1 204 No Content");
        assertThat(result).doesNotContain("Content-Length");
    }
}