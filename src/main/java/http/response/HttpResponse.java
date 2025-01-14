package http.response;

import http.enums.ContentType;
import http.enums.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import http.enums.HttpResponseStatus;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HttpResponse {
    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
    private final OutputStream out;
    private DataOutputStream dos;

    public HttpResponse(OutputStream out) {
        this.out = out;
        dos = new DataOutputStream(out);
    }

    public OutputStream getOutputStream() {
        return out;
    }

    public void sendErrorResponse(HttpResponseStatus status) throws IOException {
        String body = String.format("<h1>%s</h1>", status);
        writeResponseHeader(status, ContentType.HTML.getMimeType(), body);
        writeResponseBody(body);
    }

    public void sendSuccessResponse(HttpResponseStatus status, String type, String body) throws IOException {
        writeResponseHeader(status, type, body);
        writeResponseBody(body);
    }

    public void sendRedirectResponse(HttpResponseStatus status, String location) throws IOException {
        writeResponseHeader(status, ContentType.HTML.getMimeType(), location);
    }

    private void writeResponseHeader(HttpResponseStatus responseStatus, String contentType, String body) throws IOException {
        logger.debug("Output: " + responseStatus + " " + contentType);
        dos.writeBytes(HttpVersion.HTTP_1_1.getVersion() + " " + responseStatus.getStatusCode() + " " + responseStatus.getStatusMessage() + " \r\n");
        dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
        if (responseStatus == HttpResponseStatus.FOUND) {
            dos.writeBytes("Content-Length: 0" + "\r\n");
            dos.writeBytes("Location: " + body);
        } else {
            dos.writeBytes("Content-Length: " + body.getBytes("UTF-8").length + "\r\n");
        }
        dos.writeBytes("\r\n");
    }

    private void writeResponseBody(String body) throws IOException {
        dos.write(body.getBytes("UTF-8"));
        dos.flush();
    }
}
