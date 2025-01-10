package http;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

import java.io.DataOutputStream;
import java.io.IOException;

public class HttpResponse {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private HttpStatus status;
    private final Map<String, String> headers;
    private byte[] body;

    public HttpResponse() {
        this.status = HttpStatus.OK;
        this.headers = new HashMap<>();
    }

    public HttpResponse(HttpStatus status, String contentType, String body) {
        this();
        setStatus(status);
        setHeaders("Content-Type", contentType);
        setBody(body);
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setHeaders(String key, String value) {
        headers.put(key, value);
    }

    public void setBody(byte[] body) {
        this.body = body;
        setHeaders("Content-Length", String.valueOf(body.length));
    }

    public void setContentType(String fileExtension) {
        headers.put("Content-Type", fileExtension);
    }

    public void setBody(String bodyString) {
        byte[] content = bodyString.getBytes(StandardCharsets.UTF_8);
        setBody(content);
    }

    public void send(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 " + status.getCode() + " " + status.getMessage() + "\r\n");

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                dos.writeBytes(entry.getKey() + ": " + entry.getValue() + "\r\n");
            }
            dos.writeBytes("\r\n");

            if (body != null && body.length > 0) {
                dos.write(body, 0, body.length);
            }

            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
