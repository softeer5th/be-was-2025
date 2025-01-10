package http.request;

import static enums.HttpHeader.*;
import static util.FileUtils.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import enums.ContentType;
import enums.HttpMethod;

public class HttpRequest {
	private final int MAX_BODY_SIZE = 100 * 1024 * 1024; // 최대 100MB로 제한
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
		if (headers.containsHeader(CONTENT_LENGTH.getValue().toLowerCase())) {
			List<String> contentLengthValue = headers.getHeader(CONTENT_LENGTH.getValue());

			try {
				int contentLength = Integer.parseInt(contentLengthValue.get(0));
				if (contentLength < 0) {
					throw new IllegalArgumentException("Content-Length must not be negative");
				}

				if (contentLength > MAX_BODY_SIZE) {
					throw new IllegalArgumentException("Content-Length exceeds the maximum allowed size of " + MAX_BODY_SIZE + " bytes");
				}

				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				char[] buffer = new char[BUFFER_SIZE];
				int remaining = contentLength;

				while (remaining > 0) {
					int bytesRead = reader.read(buffer, 0, Math.min(buffer.length, remaining));
					if (bytesRead == -1) {
						throw new IOException("Unexpected end of stream");
					}
					outputStream.write(new String(buffer, 0, bytesRead).getBytes());
					remaining -= bytesRead;
				}

				this.body = outputStream.toString();

			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid Content-Length value: " + contentLengthValue.get(0), e);
			}
		}
	}

	public HttpMethod getMethod() {
		return requestLine.getMethod();
	}

	public String getPath() {
		return requestLine.getPath();
	}

	public ContentType inferContentType(){
		return requestLine.inferContentType();
	}

	public String getVersion() {
		return requestLine.getVersion();
	}

	public String getBody() {
		return body;
	}

	public boolean hasExtension() {
		return requestLine.hasExtension();
	}

	public String getPathWithoutFileName() {
		return requestLine.getPathWithoutFileName();
	}

	public String getParameter(String parameterName) {
		return requestLine.getParameter(parameterName);
	}
}

