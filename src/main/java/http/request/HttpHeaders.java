package http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeaders {
	private static final String CRLF = "\r\n";
	private final String HEADER_DELIMITER = ":";
	private final String HEADER_VALUE_DELIMITER = ",";
	private Map<String, List<String>> headers = new HashMap<>();

	public HttpHeaders(BufferedReader reader) throws IOException {
		String line;
		while ((line = reader.readLine()) != null && !line.isEmpty()) {
			String[] headerParts = line.split(HEADER_DELIMITER, 2);

			if (headerParts.length < 2) {
				throw new IllegalArgumentException("Invalid header format: " + line);
			}

			String headerName = headerParts[0].strip().toLowerCase();
			String headerValue = headerParts[1].strip();

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

	public boolean containsHeader(String headerName){
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

		if(valueList.isEmpty()){
			headers.remove(headerName);
			return;
		}

		headers.put(headerName, valueList);
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public String getHeaderToString(String headerName){
		String headerValue = String.join(HEADER_VALUE_DELIMITER, headers.get(headerName));

		return headerName + HEADER_DELIMITER + headerValue + CRLF;
	}
}
