package util;

import enums.FileContentType;
import enums.HttpStatus;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    HttpStatus status;
    Map<String, String> headers;
    byte[] body;

    public HttpResponse() {
        headers = new HashMap<>();
    }

    public HttpResponse(HttpStatus status, FileContentType contentType, String body) {
        this.status = status;
        this.headers = new HashMap<>();
        setContentType(contentType);
        setBody(body);
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setHeaders(String name, String value) {
        headers.put(name, value);
    }

    public void setBody(byte[] body) {
        this.body = body;
        setContentLength(body.length);
    }

    public void setBody(String bodyString) {
        byte[] body = bodyString.getBytes();
        this.body = body;
        setContentLength(body.length);
    }

    public void setContentType(FileContentType extension) {
        headers.put("Content-Type", extension.getContentType());
    }

    public void setContentLength(int length) {
        headers.put("Content-Length", String.valueOf(length));
    }

    public void send(DataOutputStream dos) throws IOException {
        dos.writeBytes("HTTP/1.1 " + status.getCode() + " " + status.getMessage() + " \r\n");

        for (Map.Entry<String, String> header : headers.entrySet()) {
            dos.writeBytes(header.getKey() + ": " + header.getValue() + "\r\n");
        }
        dos.writeBytes("\r\n");

        dos.write(body, 0, body.length);
        dos.writeBytes("\r\n");
        dos.flush();
    }
}
