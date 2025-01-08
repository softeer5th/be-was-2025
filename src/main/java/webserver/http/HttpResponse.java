package webserver.http;

import webserver.ContentTypeMapper;

import java.io.DataOutputStream;
import java.io.IOException;

public class HttpResponse {
    private static final String HTTP_OK = "HTTP/1.1 200 OK";
    private static final String HTTP_NOT_FOUND = "HTTP/1.1 404 Not Found";
    private final DataOutputStream dos;

    public HttpResponse(DataOutputStream dos) {
        this.dos = dos;
    }

    public void send200(byte[] body, String path) throws IOException {
        String contentType = ContentTypeMapper.getContentType(path);
        writeHeader(HTTP_OK, contentType, body.length);
        writeBody(body);
    }

    public void send404(byte[] body) throws IOException {
        writeHeader(HTTP_NOT_FOUND, "text/html", body.length);
        writeBody(body);
    }

    private void writeHeader(String status, String contentType, int contentLength) throws IOException {
        dos.writeBytes(status + "\r\n");
        dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
        dos.writeBytes("Content-Length: " + contentLength + "\r\n");
        dos.writeBytes("\r\n");
    }

    private void writeBody(byte[] body) throws IOException {
        dos.write(body);
        dos.flush();
    }
}