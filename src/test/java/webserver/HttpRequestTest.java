package webserver;

import static org.assertj.core.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

import http.request.HttpRequest;

class HttpRequestTest {

	@Test
	void testHttpRequest_SuccessCase() throws Exception {
		// 성공 케이스: 올바른 HTTP 요청
		String rawRequest = """
                GET /test/path?param1=value1&param2=value2 HTTP/1.1
                Host: localhost:8080
                Content-Length: 11

                Hello World""";

		BufferedReader reader = new BufferedReader(new StringReader(rawRequest));
		HttpRequest httpRequest = new HttpRequest(reader);

		// 메서드, 경로, 파라미터, 본문, 버전 검증
		assertThat(httpRequest.getMethod()).isEqualTo(enums.HttpMethod.GET);
		assertThat(httpRequest.getPath()).isEqualTo("/test/path");
		assertThat(httpRequest.getParameter("param1")).isEqualTo("value1");
		assertThat(httpRequest.getParameter("param2")).isEqualTo("value2");
		assertThat(httpRequest.getBody()).isEqualTo("Hello World");
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

		BufferedReader reader = new BufferedReader(new StringReader(invalidRequestLine));

		// HttpRequest 생성 시 예외 발생 검증
		assertThatThrownBy(() -> new HttpRequest(reader))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void testHttpRequest_Exception_MissingContentLength() throws Exception {
		// 실패 케이스: Content-Length 헤더가 없을 경우
		String missingContentLengthRequest = """
                POST /test/path HTTP/1.1
                Host: localhost:8080

                BodyWithoutLength""";

		BufferedReader reader = new BufferedReader(new StringReader(missingContentLengthRequest));
		HttpRequest httpRequest = new HttpRequest(reader);

		// Body는 파싱되지 않아 null 이어야 함
		assertThat(httpRequest.getBody()).isNull();
	}

	@Test
	void testHttpRequest_Exception_ContentLengthMismatch() {
		// 예외 케이스: Content-Length와 실제 Body 길이 불일치
		String mismatchedContentLengthRequest = """
                POST /test/path HTTP/1.1
                Host: localhost:8080
                Content-Length: 20

                TooShort""";

		BufferedReader reader = new BufferedReader(new StringReader(mismatchedContentLengthRequest));

		// HttpRequest 생성 시 예외 발생 검증
		assertThatThrownBy(() -> new HttpRequest(reader))
			.isInstanceOf(IOException.class);
	}
}
