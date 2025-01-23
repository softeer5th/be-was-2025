package http.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import enums.ContentType;
import enums.HttpMethod;
import util.StreamUtil;

/**
 * The type Http request line.
 */
public class HttpRequestLine {
	private static final String REQUEST_LINE_SPACE = " ";
	private static final int REQUEST_LINE_LENGTH = 3;
	private static final String QUERY_SEPARATOR = "?";
	private static final String KEY_VALUE_SEPARATOR = "=";
	private static final String PARAM_SEPARATOR = "&";

	private HttpMethod method;
	private String path;
	private String version;
	private Map<String, String> queries;

	/**
	 * Instantiates a new Http request line.
	 *
	 * @param in the in
	 * @throws IOException the io exception
	 */
	public HttpRequestLine(InputStream in) throws IOException {
		String requestLine = StreamUtil.readUntilCRLFAsString(in);

		if (requestLine == null || requestLine.isEmpty()) {
			throw new IllegalArgumentException("Empty request line");
		}

		String[] parts = requestLine.strip().split(REQUEST_LINE_SPACE);

		if (parts.length != REQUEST_LINE_LENGTH) {
			throw new IllegalArgumentException("Malformed request line: " + requestLine);
		}

		this.method = HttpMethod.valueOf(parts[0]);
		this.path = extractPath(parts[1]);
		this.version = parts[2];
		this.queries = extractQueries(parts[1]);
	}

	private static String extractPath(String fullPath) {
		int queryIndex = fullPath.indexOf(QUERY_SEPARATOR);
		return queryIndex == -1 ? fullPath : fullPath.substring(0, queryIndex);
	}

	// TODO: 동일한 키가 여러번 들어올 경우 처리 필요
	private static Map<String, String> extractQueries(String fullPath) {
		int queryIndex = fullPath.indexOf(QUERY_SEPARATOR);
		if (queryIndex == -1) {
			return Collections.emptyMap();
		}

		String queryString = fullPath.substring(queryIndex + 1);
		String[] queryPairs = queryString.split(PARAM_SEPARATOR);
		Map<String, String> queryMap = new HashMap<>();

		for (String query : queryPairs) {
			String[] keyValue = query.split(KEY_VALUE_SEPARATOR, 2);
			if (keyValue.length < 2) {
				throw new IllegalArgumentException("Malformed query: " + query);
			}
			queryMap.put(keyValue[0], keyValue[1]);

		}
		return queryMap;
	}

	/**
	 * Gets method.
	 *
	 * @return the method
	 */
	public HttpMethod getMethod() {
		return method;
	}

	/**
	 * Gets path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Gets version.
	 *
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Infer content type content type.
	 *
	 * @return the content type
	 */
	public ContentType inferContentType(){
		int extensionIndex = path.lastIndexOf(".");

		if(extensionIndex == -1){
			return ContentType.TEXT_HTML;
		}

		String extension = path.substring(extensionIndex + 1);
		return ContentType.from(extension);
	}

	/**
	 * Has extension boolean.
	 *
	 * @return the boolean
	 */
	public boolean hasExtension() {
		// TODO: 과연 이 조건만으로 정적 리소스 요청을 판단할 수 있을까?

		return path.lastIndexOf('.') != -1;
	}

	/**
	 * Gets path without file name.
	 *
	 * @return the path without file name
	 */
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

	/**
	 * Gets parameter.
	 *
	 * @param parameterName the parameter name
	 * @return the parameter
	 */
	public String getParameter(String parameterName) {
		return queries.get(parameterName);
	}
}
