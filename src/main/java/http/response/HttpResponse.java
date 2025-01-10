package http.response;

import static enums.HttpHeader.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import enums.HttpStatus;
import http.request.HttpHeaders;

public class HttpResponse {
	private static final String CRLF = "\r\n";
	private static final String STATUS_LINE_SPACE = " ";

	private String version;
	private HttpStatus statusCode;
	private HttpHeaders headers = new HttpHeaders();
	private byte[] body;

	public void sendResponse(DataOutputStream out) throws IOException {
		writeStatusLine(out);
		writeHeaders(out);
		writeBody(out);
	}

	private void writeStatusLine(DataOutputStream out) throws IOException {
		out.writeBytes(
			version + STATUS_LINE_SPACE +
				statusCode.getValue() + STATUS_LINE_SPACE +
				statusCode.getReasonPhrase() + CRLF);
	}

	private void writeHeaders(DataOutputStream out) throws IOException {
		for (Map.Entry<String, List<String>> entry : headers.getHeaders().entrySet()) {
			String headerName = entry.getKey();
			String headerToString = headers.getHeaderToString(headerName);
			out.writeBytes(headerToString); // 헤더 출력
		}
		out.writeBytes(CRLF); // 헤더와 본문 구분을 위한 공백 라인
	}

	private void writeBody(DataOutputStream out) throws IOException {
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
	public void setRedirect(String location) {
		setHeader("Location", location); // Location 헤더에 리디렉션 URL 설정
	}
}
