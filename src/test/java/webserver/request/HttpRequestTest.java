package webserver.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.common.HttpHeaders;
import webserver.enums.HttpMethod;
import webserver.enums.HttpVersion;
import webserver.exception.HttpVersionNotSupported;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpRequestTest {

    @Test
    @DisplayName("HTTP 버전 validation 테스트 - throw")
    void validationTest1() {
        // given
        try (BufferedReader body = new BufferedReader(Reader.nullReader())) {
            var httpRequest = new HttpRequest(
                    HttpMethod.GET,
                    new RequestTarget("/index.html", Map.of()),
                    HttpVersion.HTTP_0_9,
                    new HttpHeaders(),
                    body);
            var supportedVersions = List.of(HttpVersion.HTTP_1_1, HttpVersion.HTTP_1_0);
            // when
            // then
            assertThatThrownBy(() -> httpRequest.validateSupportedHttpVersion(supportedVersions))
                    .isInstanceOf(HttpVersionNotSupported.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("HTTP 버전 validation 테스트 - throw")
    void validationTest2() {
        // given
        try (BufferedReader body = new BufferedReader(Reader.nullReader())) {
            var httpRequest = new HttpRequest(
                    HttpMethod.GET,
                    new RequestTarget("/index.html", Map.of()),
                    HttpVersion.HTTP_1_1,
                    new HttpHeaders(),
                    body);
            var supportedVersions = List.of(HttpVersion.HTTP_1_1, HttpVersion.HTTP_1_0);
            // when
            // then
            httpRequest.validateSupportedHttpVersion(supportedVersions);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}