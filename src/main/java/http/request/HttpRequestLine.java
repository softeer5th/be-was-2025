package http.request;

import java.io.BufferedReader;
import java.io.IOException;

import enums.ContentType;
import enums.HttpMethod;

public class HttpRequestLine {
	private static final String REQUEST_LINE_DELIMITER = " ";
	private static final int REQUEST_LINE_LENGTH = 3;

	private HttpMethod method;
	private String path;
	private String version;

	public HttpRequestLine(BufferedReader reader) throws IOException {
		String requestLine = reader.readLine();

		if (requestLine == null || requestLine.isEmpty()) {
			throw new IllegalArgumentException("Empty request line");
		}

		String[] parts = requestLine.split(REQUEST_LINE_DELIMITER);

		if (parts.length != REQUEST_LINE_LENGTH) {
			throw new IllegalArgumentException("Malformed request line: " + requestLine);
		}

		this.method = HttpMethod.resolve(parts[0]);
		this.path = parts[1];
		this.version = parts[2];
	}

	public HttpMethod getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}

	public String getVersion() {
		return version;
	}

	public ContentType inferContentType(){
		String extention = path.substring(path.lastIndexOf(".") + 1);
		return ContentType.from(extention);
	}
}
