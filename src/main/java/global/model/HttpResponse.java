package global.model;

import webserver.ContentTypeMapper;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpResponse {
    private static final String HTTP_VERSION = "HTTP/1.1 ";
    private final DataOutputStream dos;

    public HttpResponse(DataOutputStream dos) {
        this.dos = dos;
    }

    public void send200(byte[] body, String path) throws IOException {
        String contentType = ContentTypeMapper.getContentType(path);
        writeHeader(
                HTTP_VERSION + HttpStatus.OK.getStatusLine(),
                contentType,
                body.length,
                null
        );
        writeBody(body);
    }

    public void send404(byte[] body) throws IOException {
        writeHeader(
                HTTP_VERSION + HttpStatus.NOT_FOUND.getStatusLine(),
                "text/html",
                body.length,
                null
        );
        writeBody(body);
    }

    public void sendJson(String json) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        writeHeader(
                HTTP_VERSION + HttpStatus.OK.getStatusLine(),
                "application/json",
                body.length,
                null
        );
        writeBody(body);
    }

    public void sendJsonWithCookie(String json, String cookie) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);

        writeHeader(
                HTTP_VERSION + HttpStatus.OK.getStatusLine(),
                "application/json;charset=utf-8",
                body.length,
                cookie
        );

        writeBody(body);
    }

    public void sendRedirect(String location) throws IOException {
        dos.writeBytes(HTTP_VERSION + HttpStatus.FOUND.getStatusLine() + "\r\n");
        dos.writeBytes("Location: " + location + "\r\n");
        dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
        dos.writeBytes("\r\n");
        dos.flush();
    }

    private void writeHeader(String status, String contentType, int contentLength, String cookie) throws IOException {
        dos.writeBytes(status + "\r\n");
        dos.writeBytes("Content-Type: " + contentType + "\r\n");
        dos.writeBytes("Content-Length: " + contentLength + "\r\n");

        if (cookie != null && !cookie.isBlank()) {
            dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
        }

        dos.writeBytes("\r\n");
    }

    private void writeBody(byte[] body) throws IOException {
        dos.write(body);
        dos.flush();
    }
}