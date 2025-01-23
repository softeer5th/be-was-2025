package http.response;

import static enums.HttpHeader.*;

import java.io.IOException;
import java.io.OutputStream;

import enums.HttpStatus;
import http.request.HttpHeaders;
import view.View;

public class HttpResponse {
	private static final String CRLF = "\r\n";
	private static final String STATUS_LINE_SPACE = " ";

	private String version;
	private HttpStatus statusCode = HttpStatus.OK;
	private HttpHeaders headers = new HttpHeaders();
	private byte[] body;
	private View view;

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

	public void setVersion(String version) {
		this.version = version;
	}

	public void setStatusCode(HttpStatus statusCode) {
		this.statusCode = statusCode;
	}

	public void setHeader(String headerName, String... values) {
		headers.setHeader(headerName, values);
	}

	public void setBody(byte[] body) {
		this.body = body;
		setHeader(CONTENT_LENGTH.getValue(), String.valueOf(body.length));
	}
	// sendRedirect 메서드 구현
	private void setRedirect(String location) {
		setHeader(LOCATION.name(), location); // Location 헤더에 리디렉션 URL 설정
	}

	public void setRedirectResponse(HttpResponse response, String version, HttpStatus status, String location) {
		response.setStatusCode(status);
		response.setVersion(version);
		response.setRedirect(location);
	}

	public void setErrorResponse(HttpResponse response, String version, HttpStatus status, String message) {
		response.setStatusCode(status);
		response.setVersion(version);
		response.setBody(message.getBytes());
	}

	public void setCookie(String name, String value, String... options) {
		headers.setCookie(name, value, options);
	}

	public String getBodyToString() {
		return new String(body);
	}


	public void setView(View view) {
		this.view = view;
	}

	public View getView() {
		return view;
	}
}
