package http.response;

import static enums.HttpHeader.*;

import java.io.IOException;
import java.io.OutputStream;

import enums.HttpStatus;
import http.request.HttpHeaders;
import view.View;

/**
 * The type Http response.
 */
public class HttpResponse {
	private static final String CRLF = "\r\n";
	private static final String STATUS_LINE_SPACE = " ";

	private String version;
	private HttpStatus statusCode = HttpStatus.OK;
	private HttpHeaders headers = new HttpHeaders();
	private byte[] body;
	private View view;

	/**
	 * Send response.
	 *
	 * @param out the out
	 * @throws IOException the io exception
	 */
	public void sendResponse(OutputStream out) throws IOException {
		writeStatusLine(out);
		writeHeaders(out);
		writeBody(out);
	}

	private void writeStatusLine(OutputStream out) throws IOException {
		String s = version + STATUS_LINE_SPACE +
			statusCode.getValue() + STATUS_LINE_SPACE +
			statusCode.getReasonPhrase() + CRLF;
		out.write(s.getBytes());
	}

	private void writeHeaders(OutputStream out) throws IOException {
		out.write(headers.toMessage().getBytes());
		out.write(CRLF.getBytes()); // 헤더와 본문 구분을 위한 공백 라인
	}

	private void writeBody(OutputStream out) throws IOException {
		if (body != null && body.length > 0) {
			out.write(body);
		}
		out.flush();
	}

	/**
	 * Sets version.
	 *
	 * @param version the version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Sets status code.
	 *
	 * @param statusCode the status code
	 */
	public void setStatusCode(HttpStatus statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * Sets header.
	 *
	 * @param headerName the header name
	 * @param values the values
	 */
	public void setHeader(String headerName, String... values) {
		headers.setHeader(headerName, values);
	}

	/**
	 * Sets body.
	 *
	 * @param body the body
	 */
	public void setBody(byte[] body) {
		this.body = body;
		setHeader(CONTENT_LENGTH.getValue(), String.valueOf(body.length));
	}
	// sendRedirect 메서드 구현
	private void setRedirect(String location) {
		setHeader(LOCATION.name(), location); // Location 헤더에 리디렉션 URL 설정
	}

	/**
	 * Sets redirect response.
	 *
	 * @param response the response
	 * @param version the version
	 * @param status the status
	 * @param location the location
	 */
	public void setRedirectResponse(HttpResponse response, String version, HttpStatus status, String location) {
		response.setStatusCode(status);
		response.setVersion(version);
		response.setRedirect(location);
	}

	/**
	 * Sets error response.
	 *
	 * @param response the response
	 * @param version the version
	 * @param status the status
	 * @param message the message
	 */
	public void setErrorResponse(HttpResponse response, String version, HttpStatus status, String message) {
		response.setStatusCode(status);
		response.setVersion(version);
		response.setBody(message.getBytes());
	}

	/**
	 * Sets cookie.
	 *
	 * @param name the name
	 * @param value the value
	 * @param options the options
	 */
	public void setCookie(String name, String value, String... options) {
		headers.setCookie(name, value, options);
	}

	/**
	 * Gets body to string.
	 *
	 * @return the body to string
	 */
	public String getBodyToString() {
		return new String(body);
	}

	/**
	 * Sets view.
	 *
	 * @param view the view
	 */
	public void setView(View view) {
		this.view = view;
	}

	/**
	 * Gets view.
	 *
	 * @return the view
	 */
	public View getView() {
		return view;
	}
}
