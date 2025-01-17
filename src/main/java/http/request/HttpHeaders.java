package http.request;

import static enums.HttpHeader.*;
import static http.HttpSessionStorage.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import http.Cookies;
import util.StreamUtil;

public class HttpHeaders {
	private static final String CRLF = "\r\n";
	public static final String HEADER_DELIMITER = ":";
	private static final String HEADER_SPACE = " ";
	private static final String HEADER_VALUE_DELIMITER = ",";
	private Map<String, List<String>> headers = new HashMap<>();
	private Cookies cookies = new Cookies();

	public HttpHeaders(InputStream in) throws IOException {
		// HTTP Header는 대부분 문자 기반의 데이터를 포함하고 있기 때문에, 문자 단위로 처리하는 것이 더 적합하다고 판단.

		String line;
		while ((line = StreamUtil.readUntilCRLFAsString(in)) != null && !line.isEmpty()) {
			String[] headerParts = line.split(HEADER_DELIMITER, 2);

			if (headerParts[0].equals(CRLF)) {
				break;
			}

			if (headerParts.length < 2) {
				throw new IllegalArgumentException("Invalid header format: " + line);
			}

			String headerName = headerParts[0].strip().toLowerCase();
			String headerValue = headerParts[1].strip();

			if(headerName.equals(COOKIE.name().toLowerCase())) {
				cookies = new Cookies(headerParts[1]);
				continue;
			}

			if (headerName.isEmpty() || headerValue.isEmpty()) {
				throw new IllegalArgumentException("Invalid header format: " + line);
			}

			List<String> valueList = headers.getOrDefault(headerName, new ArrayList<>());
			String[] values = headerValue.split(HEADER_VALUE_DELIMITER);
			for (String value : values) {
				String strippedValue = value.strip();
				if (!strippedValue.isEmpty()) {
					valueList.add(strippedValue);
				}
			}

			headers.put(headerName, valueList);
		}
	}

	public HttpHeaders() {
	}

	public boolean containsHeader(String headerName) {
		return headers.containsKey(headerName);
	}

	public List<String> getHeader(String name) {
		return headers.get(name.toLowerCase());
	}

	public void setHeader(String headerName, String... values) {
		headerName = headerName.toLowerCase();
		List<String> valueList = headers.getOrDefault(headerName, new ArrayList<>());

		for (String value : values) {
			String strippedValue = value.strip();
			if (!strippedValue.isEmpty()) {
				valueList.add(strippedValue);
			}
		}

		if (valueList.isEmpty()) {
			headers.remove(headerName);
			return;
		}

		headers.put(headerName, valueList);
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public void setCookie(String name, String value, String... options) {
		cookies.setCookie(name, value, options);
	}

	public String toMessage(){
		StringBuilder sb = new StringBuilder();

		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
			String headerName = entry.getKey();
			String headerValue = String.join(HEADER_VALUE_DELIMITER, headers.get(headerName));
			sb.append(headerName).append(HEADER_DELIMITER).append(HEADER_SPACE).append(headerValue).append(CRLF);
		}

		if (cookies.hasCookie()) {
			sb.append(cookies.toMessage());
		}

		return sb.toString();
	}

	public String getSessionId() {
		return cookies.getCookie(SESSION_ID);
	}
}
