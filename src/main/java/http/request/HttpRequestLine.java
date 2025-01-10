package http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import enums.ContentType;
import enums.HttpMethod;

public class HttpRequestLine {
	private static final String REQUEST_LINE_DELIMITER = " ";
	private static final int REQUEST_LINE_LENGTH = 3;
	private static final String QUERY_SEPARATOR = "?";
	private static final String KEY_VALUE_SEPARATOR = "=";
	private static final String PARAM_SEPARATOR = "&";

	private HttpMethod method;
	private String path;
	private String version;
	private Map<String, String> queries;

	public HttpRequestLine(BufferedReader reader) throws IOException {
		String requestLine = reader.readLine();

		if (requestLine == null || requestLine.isEmpty()) {
			throw new IllegalArgumentException("Empty request line");
		}

		String[] parts = requestLine.split(REQUEST_LINE_DELIMITER);

		if (parts.length != REQUEST_LINE_LENGTH) {
			throw new IllegalArgumentException("Malformed request line: " + requestLine);
		}

		this.method = HttpMethod.valueOf(parts[0]);
		this.path = parts[1];
		this.version = parts[2];
		queries = getQueries(this.path);

		if(queries.size() > 0){
			this.path = this.path.substring(0, this.path.indexOf(QUERY_SEPARATOR));
		}
	}

	// TODO: 동일한 키가 여러번 들어올 경우 처리 필요
	private static Map<String, String> getQueries(String path) {
		Map<String, String> requestQueries = new HashMap<>();

		if (path.contains(QUERY_SEPARATOR)) {
			int queryStartIndex = path.indexOf(QUERY_SEPARATOR) + 1;
			String requestQueryString = path.substring(queryStartIndex);
			String[] queries = requestQueryString.split(PARAM_SEPARATOR);

			for (String query : queries) {
				String[] q = query.split(KEY_VALUE_SEPARATOR);
				String key = q[0];
				String value = q[1];
				requestQueries.put(key, value);
			}
		}
		return requestQueries;
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
		int extensionIndex = path.lastIndexOf('.');
		if(extensionIndex == -1){
			return path;
		}

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

	public String getParameter(String parameterName) {
		return queries.get(parameterName);
	}
}
