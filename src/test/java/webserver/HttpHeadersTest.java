package webserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.*;

import http.request.HttpHeaders;

class HttpHeadersTest {
	private HttpHeaders httpHeaders;

	@BeforeEach
	void setUp() {
		httpHeaders = new HttpHeaders();
	}

	@Test
	void testInitializeFromReader_Success() throws Exception {
		String headerString = """
                Host: localhost\r
                Content-Type: text/html; charset=UTF-8\r
                Connection: keep-alive\r
                \r
                """;

		InputStream inputStream = new ByteArrayInputStream(headerString.getBytes());
		HttpHeaders headers = new HttpHeaders(inputStream);

		assertThat(headers.containsHeader("host")).isTrue();
		assertThat(headers.getHeader("host")).containsExactly("localhost");
		assertThat(headers.getHeader("content-type")).containsExactly("text/html; charset=UTF-8");
		assertThat(headers.getHeader("connection")).containsExactly("keep-alive");
	}

	@Test
	void testInitializeWithInvalidHeaderFormat() {
		String invalidHeaderString = """
                InvalidHeaderWithoutColon
                AnotherInvalidHeader
                """;

		InputStream inputStream = new ByteArrayInputStream(invalidHeaderString.getBytes());

		assertThatThrownBy(() -> new HttpHeaders(inputStream))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void testEmptyHeaderName() throws Exception {
		String headerString = """
                : empty-header-value
                """;

		InputStream inputStream = new ByteArrayInputStream(headerString.getBytes());

		assertThatThrownBy(() -> new HttpHeaders(inputStream))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void testHeaderWithEmptyValue() {
		httpHeaders.setHeader("Custom-Header", "");

		assertThat(httpHeaders.containsHeader("custom-header")).isFalse();
	}

	@Test
	void testDuplicateHeaderName() {
		httpHeaders.setHeader("Content-Type", "text/plain");
		httpHeaders.setHeader("Content-Type", "application/json");

		assertThat(httpHeaders.getHeader("content-type")).containsExactly("text/plain", "application/json");
	}

	@Test
	void testHeaderCaseInsensitiveBehavior() {
		httpHeaders.setHeader("CONTENT-TYPE", "text/html");

		assertThat(httpHeaders.containsHeader("content-type")).isTrue();
		assertThat(httpHeaders.getHeader("content-type")).containsExactly("text/html");
	}

	@Test
	void testEmptyHeaderBlock() throws Exception {
		String headerString = """
                """;

		InputStream inputStream = new ByteArrayInputStream(headerString.getBytes());
		HttpHeaders headers = new HttpHeaders(inputStream);

		assertThat(headers.getHeaders()).isEmpty();
	}
}

