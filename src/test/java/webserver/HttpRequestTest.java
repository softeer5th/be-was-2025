package webserver;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import http.request.HttpRequest;

class HttpRequestTest {

	@Test
	void testHttpRequest_SuccessCase() throws Exception {
		// 성공 케이스: 올바른 HTTP 요청
		String rawRequest = """
                GET /test/path?param1=value1&param2=value2 HTTP/1.1\r
                Host: localhost:8080\r
                Content-Length: 11\r
                \r
                Hello World""";

		InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes());
		HttpRequest httpRequest = new HttpRequest(inputStream);

		// 메서드, 경로, 파라미터, 본문, 버전 검증
		assertThat(httpRequest.getMethod()).isEqualTo(enums.HttpMethod.GET);
		assertThat(httpRequest.getPath()).isEqualTo("/test/path");
		assertThat(httpRequest.getParameter("param1")).isEqualTo("value1");
		assertThat(httpRequest.getParameter("param2")).isEqualTo("value2");
		String actual = new String(httpRequest.getBody(), StandardCharsets.UTF_8);
		assertThat(actual).isEqualTo("Hello World");
		assertThat(httpRequest.getVersion()).isEqualTo("HTTP/1.1");
	}

	@Test
	void testHttpRequest_Fail_InvalidHttpRequestLine() {
		// 실패 케이스: 잘못된 HTTP Request Line
		String invalidRequestLine = """
                GET_invalid /invalid HTTP/1.1
                Host: localhost:8080
                Content-Length: 5

                Hello""";

		InputStream inputStream = new ByteArrayInputStream(invalidRequestLine.getBytes());

		// HttpRequest 생성 시 예외 발생 검증
		assertThatThrownBy(() -> new HttpRequest(inputStream))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void testHttpRequest_Exception_MissingContentLength() throws Exception {
		// 실패 케이스: Content-Length 헤더가 없을 경우
		String missingContentLengthRequest = """
                POST /test/path HTTP/1.1\r
                Host: localhost:8080\r
                \r
                BodyWithoutLength""";

		InputStream inputStream = new ByteArrayInputStream(missingContentLengthRequest.getBytes());
		HttpRequest httpRequest = new HttpRequest(inputStream);

		// Body는 파싱되지 않아 null 이어야 함
		assertThat(httpRequest.getBody()).isNull();
	}

}
