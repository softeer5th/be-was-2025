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
		int extensionIndex = path.lastIndexOf(".");

		if(extensionIndex == -1){
			return ContentType.TEXT_HTML;
		}

		String extension = path.substring(extensionIndex + 1);
		return ContentType.from(extension);
	}

	public boolean hasExtension() {
		// TODO: 과연 이 조건만으로 정적 리소스 요청을 판단할 수 있을까?

		return path.lastIndexOf('.') != -1;
	}

	public String getPathWithoutFileName() {
		int lastSlashIndex = path.lastIndexOf('/');

		if(lastSlashIndex == 0){
			return "/";
		}

		// 마지막 슬래시 이후에 파일명이 있으면 그 부분을 잘라냄
		if (lastSlashIndex != -1) {
			return path.substring(0, lastSlashIndex);
		}
		return path;  // '/'만 있을 경우 그대로 반환
	}
}
