package webserver.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.enums.HttpMethod;
import webserver.enums.HttpVersion;
import webserver.exception.HttpVersionNotSupported;
import webserver.header.RequestHeader;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class HttpRequestTest {

    @Test
    @DisplayName("HTTP 버전 validation 테스트 - throw")
    void validationTest1() {
        // given
        var httpRequest = new HttpRequest(
                HttpMethod.GET,
                new RequestTarget("/index.html", Map.of()),
                HttpVersion.HTTP_0_9,
                new RequestHeader(),
                mock());
        var supportedVersions = List.of(HttpVersion.HTTP_1_1, HttpVersion.HTTP_1_0);
        // when
        // then
        assertThatThrownBy(() -> httpRequest.validateSupportedHttpVersion(supportedVersions))
                .isInstanceOf(HttpVersionNotSupported.class);

    }

    @Test
    @DisplayName("HTTP 버전 validation 테스트 - throw")
    void validationTest2() {
        // given
        var httpRequest = new HttpRequest(
                HttpMethod.GET,
                new RequestTarget("/index.html", Map.of()),
                HttpVersion.HTTP_1_1,
                new RequestHeader(),
                mock());
        var supportedVersions = List.of(HttpVersion.HTTP_1_1, HttpVersion.HTTP_1_0);
        // when
        // then
        httpRequest.validateSupportedHttpVersion(supportedVersions);

    }


}