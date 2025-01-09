package http.request;

import static enums.HttpHeader.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import enums.ContentType;
import enums.HttpMethod;

public class HttpRequest {
	private HttpRequestLine requestLine;
	private HttpHeaders headers;
	private String body;  // 추후에 byte[] 나 request Accept 에 맞게 변형

	public HttpRequest(BufferedReader reader) throws IOException {
		parseRequest(reader);
	}

	private void parseRequest(BufferedReader reader) throws IOException {
		this.requestLine = new HttpRequestLine(reader);
		this.headers = new HttpHeaders(reader);

		// Body 파싱 (추후에 분리 고려)
		parseBody(reader);
	}

	private void parseBody(BufferedReader reader) throws IOException {
		// Content-Length를 읽어와 Body 크기 결정
		if (headers.containsHeader(CONTENT_LENGTH.getValue())) {
			List<String> contentLengthValue = headers.getHeader(CONTENT_LENGTH.getValue());
			int contentLength = Integer.parseInt(contentLengthValue.get(0));

			char[] bodyChars = new char[contentLength];
			int bytesRead = reader.read(bodyChars, 0, contentLength);
			if (bytesRead > 0) {
				this.body = new String(bodyChars, 0, bytesRead);
			}
		}

	}

	public HttpMethod getMethod() {
		return requestLine.getMethod();
	}

	public String getPath() {
		return requestLine.getUri();
	}

	public ContentType inferContentType(){
		return requestLine.inferContentType();
	}

	public String getVersion() {
		return requestLine.getVersion();
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

	public String getBody() {
		return body;
	}
}

