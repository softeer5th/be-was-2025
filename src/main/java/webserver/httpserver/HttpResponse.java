package webserver.httpserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    public static final String HEADER_DELIMITER = ": ";
    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
    private String protocol;
    private StatusCode statusCode;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> cookies = new HashMap<>();
    private byte[] body;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getCookie(String key) {
        return cookies.get(key);
    }

    public void setCookie(String key, String value) {
        cookies.put(key, value);
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
        if (body != null) {
            headers.put("Content-Length", String.valueOf(body.length));
        }
    }

    public void send(DataOutputStream dos) throws IOException {
        dos.writeBytes(protocol + " " + statusCode.code + " " + statusCode.message + "\n");
        for (Map.Entry<String, String> header : headers.entrySet()) {
            dos.writeBytes(header.getKey() + HEADER_DELIMITER + header.getValue() + "\n");
        }
        dos.writeBytes("\n");
        if (body != null && body.length > 0) {
            dos.write(body, 0, body.length);
            dos.writeBytes("\n");
        }
        dos.flush();
    }
}
