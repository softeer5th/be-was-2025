package webserver.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.enums.HttpStatusCode;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static webserver.enums.ContentType.TEXT_HTML;
import static webserver.enums.ContentType.TEXT_PLAIN;
import static webserver.enums.HttpHeader.CONTENT_LENGTH;
import static webserver.enums.HttpHeader.CONTENT_TYPE;

class HttpResponseTest {

    @Test
    @DisplayName("File Body")
    void test1() {
        // given
        HttpResponse httpResponse = new HttpResponse(HttpStatusCode.OK);
        File body = new File("/a/b/index.html");

        // when
        httpResponse.setBody(body);

        // then

        assertThat(httpResponse.getHeaders())
                .containsEntry(CONTENT_TYPE.value, TEXT_HTML.mimeType)
                .containsKeys(CONTENT_LENGTH.value);
    }

    @Test
    @DisplayName("String Body")
    void test2() {
        // given
        HttpResponse httpResponse = new HttpResponse(HttpStatusCode.OK);
        String body = "Hello, World!";

        // when
        httpResponse.setBody(body);

        // then
        assertThat(httpResponse.getHeaders())
                .containsEntry(CONTENT_TYPE.value, TEXT_PLAIN.mimeType)
                .containsEntry(CONTENT_LENGTH.value, "13");
    }

    @Test
    @DisplayName("Empty Body")
    void test3() {
        // given
        HttpResponse httpResponse = new HttpResponse(HttpStatusCode.OK);

        // when

        // then
        assertThat(httpResponse.getHeaders())
                .doesNotContainKey(CONTENT_TYPE.value)
                .doesNotContainKey(CONTENT_LENGTH.value);
    }

}