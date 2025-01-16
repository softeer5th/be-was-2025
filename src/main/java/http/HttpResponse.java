package http;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;

public class HttpResponse {

    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
    private static final String NEW_LINE = "\r\n";

    private HttpStatus status;
    private final Map<String, String> headers;
    private byte[] body;
    private final List<String> cookies = new ArrayList<>();

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

    public void setCookies(String value) {
        cookies.add(value);
    }

    public void setBody(String bodyString) {
        byte[] content = bodyString.getBytes(StandardCharsets.UTF_8);
        setBody(content);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public byte[] getBody() {
        return body;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public void send(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 " + status.getCode() + " " + status.getMessage() + NEW_LINE);

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                dos.writeBytes(entry.getKey() + ": " + entry.getValue() + NEW_LINE);
            }

            dos.writeBytes("Set-Cookie: ");
            int cookieCount = cookies.size();
            for (int i = 0; i < cookieCount; i++) {
                dos.writeBytes(cookies.get(i));
                if (i < cookieCount - 1) {
                    dos.writeBytes("; ");
                }
            }

            dos.writeBytes(NEW_LINE + NEW_LINE);
            if (body != null && body.length > 0) {
                dos.write(body, 0, body.length);
            }

            dos.flush();
        } catch (IOException e) {
            logger.error("Response send error : {}", e.getMessage());
        }
    }
}
