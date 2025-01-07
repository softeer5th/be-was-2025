package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
	private HttpMethod method;
	private String path;
	private String version;
	private Map<String, String> headers = new HashMap<>();
	private String body;

	public HttpRequest(BufferedReader reader) throws IOException {
		parseRequest(reader);
	}

	private void parseRequest(BufferedReader reader) throws IOException {
		// 첫 번째 라인 (Request Line) 파싱
		String requestLine = reader.readLine();
		if (requestLine == null || requestLine.isEmpty()) {
			throw new IOException("Empty request line");
		}

		String[] parts = requestLine.split(" ");
		if (parts.length != 3) {
			throw new IOException("Malformed request line: " + requestLine);
		}

		this.method = HttpMethod.resolve(parts[0]);
		this.path = parts[1];
		this.version = parts[2];

		// 헤더 파싱
		String line;
		while (!(line = reader.readLine()).isEmpty()) {
			String[] headerParts = line.split(": ", 2);
			if (headerParts.length == 2) {
				headers.put(headerParts[0], headerParts[1]);
			}
		}

		// Body 파싱 (필요 시)
		if (headers.containsKey("Content-Length")) {
			int contentLength = Integer.parseInt(headers.get("Content-Length"));
			char[] bodyChars = new char[contentLength];
			reader.read(bodyChars);
			this.body = new String(bodyChars);
		}
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

	public Map<String, String> getHeaders() {
		return headers;
	}

	public String getBody() {
		return body;
	}
}

