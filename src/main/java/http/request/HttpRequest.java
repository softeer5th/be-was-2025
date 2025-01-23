package http.request;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import enums.ContentType;
import enums.HttpMethod;

/**
 * The type Http request.
 */
public class HttpRequest {
	private HttpRequestLine requestLine;
	private HttpHeaders headers;
	private HttpRequestBody body;

	/**
	 * Instantiates a new Http request.
	 *
	 * @param in the in
	 * @throws IOException the io exception
	 */
	public HttpRequest(InputStream in) throws IOException {
		parseRequest(in);
	}

	private void parseRequest(InputStream in) throws IOException {
		this.requestLine = new HttpRequestLine(in);
		this.headers = new HttpHeaders(in);
		this.body = new HttpRequestBody(in, this.headers);
	}

	/**
	 * Gets method.
	 *
	 * @return the method
	 */
	public HttpMethod getMethod() {
		return requestLine.getMethod();
	}

	/**
	 * Gets path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return requestLine.getPath();
	}

	/**
	 * Infer content type content type.
	 *
	 * @return the content type
	 */
	public ContentType inferContentType(){
		return requestLine.inferContentType();
	}

	/**
	 * Gets version.
	 *
	 * @return the version
	 */
	public String getVersion() {
		return requestLine.getVersion();
	}

	/**
	 * Gets body as map.
	 *
	 * @return the body as map
	 */
	public Optional<Map<String, String>> getBodyAsMap() {
		return body.getBodyAsMap();
	}

	/**
	 * Gets body as string.
	 *
	 * @return the body as string
	 */
	public String getBodyAsString() {
		return body.getBodyAsString();
	}

	/**
	 * Get body as byte array byte [ ].
	 *
	 * @return the byte [ ]
	 */
	public byte[] getBodyAsByteArray() {
		return body.getBodyAsByteArray();
	}

	/**
	 * Has extension boolean.
	 *
	 * @return the boolean
	 */
	public boolean hasExtension() {
		return requestLine.hasExtension();
	}

	/**
	 * Gets path without file name.
	 *
	 * @return the path without file name
	 */
	public String getPathWithoutFileName() {
		return requestLine.getPathWithoutFileName();
	}

	/**
	 * Gets parameter.
	 *
	 * @param parameterName the parameter name
	 * @return the parameter
	 */
	public String getParameter(String parameterName) {
		return requestLine.getParameter(parameterName);
	}

	/**
	 * Gets session id.
	 *
	 * @return the session id
	 */
	public String getSessionId() {
		return headers.getSessionId();
	}

	/**
	 * Get body as multipart list.
	 *
	 * @return the list
	 */
	public List<Map<String, Object>> getBodyAsMultipart(){
		return body.getBodyAsMultipart(headers);
	}
}

