package response;

import enums.FileContentType;
import enums.HttpStatus;
import exception.ServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static exception.ErrorCode.KEY_VALUE_SHOULD_BE_EVEN;

public class HttpResponse {
    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);

    HttpStatus status;
    Map<String, String> headers;
    byte[] body;

    public HttpResponse() {
        headers = new HashMap<>();
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getHeaderValue(String name) {
        return headers.get(name);
    }

    public byte[] getBody() {
        return body;
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

    public void setHeaders(String... keyValues) {
        if (keyValues.length % 2 != 0) {
            throw new ServerErrorException(KEY_VALUE_SHOULD_BE_EVEN);
        }
        for (int i = 0; i < keyValues.length; i += 2) {
            String key = keyValues[i];
            String value = keyValues[i + 1];
            headers.put(key, value);
        }
    }

    public void setBody(byte[] body) {
        this.body = body;
        setContentLength(body.length);
    }

    public void setBody(String bodyString) {
        byte[] body = bodyString.getBytes();
        setBody(body);
    }

    public void setContentType(FileContentType extension) {
        headers.put("Content-Type", extension.getContentType());
    }

    public void setContentLength(int length) {
        headers.put("Content-Length", String.valueOf(length));
    }

    public void setResponse(HttpStatus status, FileContentType contentType) {
        setStatus(status);
        setContentType(contentType);
    }

    public void setResponse(HttpStatus status, FileContentType contentType, String body) {
        setStatus(status);
        setContentType(contentType);
        setBody(body);
    }

    public void setResponse(HttpStatus status, FileContentType contentType, byte[] body) {
        setStatus(status);
        setContentType(contentType);
        setBody(body);
    }

    public void send(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 " + status.getCode() + " " + status.getMessage() + " \r\n");

            for (Map.Entry<String, String> header : headers.entrySet()) {
                dos.writeBytes(header.getKey() + ": " + header.getValue() + "\r\n");
            }
            dos.writeBytes("\r\n");

            if (body!= null && body.length > 0)
                dos.write(body, 0, body.length);
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
