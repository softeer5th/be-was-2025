package http.request;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import enums.ContentType;
import enums.HttpMethod;

public class HttpRequest {
	private HttpRequestLine requestLine;
	private HttpHeaders headers;
	private HttpRequestBody body;

	public HttpRequest(InputStream in) throws IOException {
		parseRequest(in);
	}

	private void parseRequest(InputStream in) throws IOException {
		this.requestLine = new HttpRequestLine(in);
		this.headers = new HttpHeaders(in);
		this.body = new HttpRequestBody(in, this.headers);
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

	public Optional<Map<String, String>> getBodyAsMap() {
		return body.getBodyAsMap();
	}

	public String getBodyAsString() {
		return body.getBodyAsString();
	}

	public byte[] getBodyAsByteArray() {
		return body.getBodyAsByteArray();
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
	public String getSessionId() {
		return headers.getSessionId();
	}
	public List<Map<String, String>> getBodyAsMultipart(){
		return body.getBodyAsMultipart(headers);
	}
}

