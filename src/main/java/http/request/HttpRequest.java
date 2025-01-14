package http.request;

import static enums.HttpHeader.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import enums.ContentType;
import enums.HttpMethod;

public class HttpRequest {
	private final int MAX_BODY_SIZE = 100 * 1024 * 1024; // 최대 100MB로 제한
	private HttpRequestLine requestLine;
	private HttpHeaders headers;
	private byte[] body;

	public HttpRequest(InputStream in) throws IOException {
		parseRequest(in);
	}

	private void parseRequest(InputStream in) throws IOException {
		this.requestLine = new HttpRequestLine(in);
		this.headers = new HttpHeaders(in);

		parseBody(in);
	}

	private void parseBody(InputStream in) throws IOException {
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

				this.body = readBody(in, contentLength);

			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid Content-Length value: " + contentLengthValue.get(0), e);
			}
		}
	}

	private byte[] readBody(InputStream in, int contentLength) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[MAX_BODY_SIZE]; // 8KB buffer
		int remaining = contentLength;

		while (remaining > 0) {
			int bytesRead = in.read(buffer, 0, Math.min(buffer.length, remaining));
			if (bytesRead == -1) {
				throw new IOException("Unexpected end of stream");
			}
			outputStream.write(buffer, 0, bytesRead);
			remaining -= bytesRead;
		}

		return outputStream.toByteArray();
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

	public byte[] getBody() {
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

