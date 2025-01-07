package http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HttpResponse {
	private final DataOutputStream dos;

	public HttpResponse(OutputStream out) {
		this.dos = new DataOutputStream(out);
	}

	public void sendResponse(HttpStatus httpStatus, String contentType, byte[] body) throws IOException {
		writeStatusLine(httpStatus.getValue(), httpStatus.getReasonPhrase());
		writeHeaders(contentType, body.length);
		writeBody(body);
	}

	public void send404() throws IOException {
		String body = "<h1>404 Not Found</h1>";
		sendResponse(HttpStatus.NOT_FOUND, "text/html", body.getBytes());
	}

	private void writeStatusLine(int statusCode, String statusMessage) throws IOException {
		dos.writeBytes(String.format("HTTP/1.1 %d %s\r\n", statusCode, statusMessage));
	}

	private void writeHeaders(String contentType, int contentLength) throws IOException {
		dos.writeBytes(String.format("Content-Type: %s; charset=utf-8\r\n", contentType));
		dos.writeBytes(String.format("Content-Length: %d\r\n", contentLength));
		dos.writeBytes("\r\n");
	}

	private void writeBody(byte[] body) throws IOException {
		if (body != null && body.length > 0) {
			dos.write(body);
		}
		dos.flush();
	}
}
