package webserver;

import enums.ContentType;
import enums.HttpMethod;
import http.request.HttpRequestLine;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpRequestLineTest {

	@Test
	void validRequestLine_successCase() throws Exception {
		String requestLine = "GET /index.html?key=value&name=user HTTP/1.1";
		BufferedReader reader = new BufferedReader(new StringReader(requestLine));
		HttpRequestLine httpRequestLine = new HttpRequestLine(reader);

		assertThat(httpRequestLine.getMethod()).isEqualTo(HttpMethod.GET);
		assertThat(httpRequestLine.getPath()).isEqualTo("/index.html");
		assertThat(httpRequestLine.getVersion()).isEqualTo("HTTP/1.1");
		assertThat(httpRequestLine.getParameter("key")).isEqualTo("value");
		assertThat(httpRequestLine.getParameter("name")).isEqualTo("user");
		assertThat(httpRequestLine.inferContentType()).isEqualTo(ContentType.TEXT_HTML);
	}

	@Test
	void emptyRequestLine_throwsException() {
		String requestLine = "";
		BufferedReader reader = new BufferedReader(new StringReader(requestLine));

		assertThatThrownBy(() -> new HttpRequestLine(reader))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Empty request line");
	}

	@Test
	void malformedRequestLine_throwsException() {
		String requestLine = "GET /index.html"; // HTTP version missing
		BufferedReader reader = new BufferedReader(new StringReader(requestLine));

		assertThatThrownBy(() -> new HttpRequestLine(reader))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Malformed request line");
	}

	@Test
	void duplicateQueryKeys_overridesPreviousValue() throws Exception {
		String requestLine = "GET /index.html?key=value1&key=value2 HTTP/1.1";
		BufferedReader reader = new BufferedReader(new StringReader(requestLine));
		HttpRequestLine httpRequestLine = new HttpRequestLine(reader);

		assertThat(httpRequestLine.getParameter("key")).isEqualTo("value2");
	}

	@Test
	void invalidQueryFormat_throwsException() {
		String requestLine = "GET /index.html?keyvalue HTTP/1.1"; // Missing '='
		BufferedReader reader = new BufferedReader(new StringReader(requestLine));

		assertThatThrownBy(() -> new HttpRequestLine(reader))
			.isInstanceOf(ArrayIndexOutOfBoundsException.class);
	}
}

