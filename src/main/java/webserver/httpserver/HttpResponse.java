package webserver.httpserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.httpserver.header.Cookie;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {
    public static final String HEADER_DELIMITER = ": ";
    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
    private String protocol;
    private StatusCode statusCode;
    private final Map<String, String> headers = new HashMap<>();
    private final List<Cookie> cookies = new ArrayList<>();
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

    public void setCookie(Cookie cookie) {
        cookies.add(cookie);
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

    public void setLocation(String location) {
        setStatusCode(StatusCode.SEE_OTHER);
        setHeader("Location", location);
    }

    public void send(DataOutputStream dos) throws IOException {
        dos.writeBytes(protocol + " " + statusCode.code + " " + statusCode.message + "\n");
        for (Map.Entry<String, String> header : headers.entrySet()) {
            dos.writeBytes(header.getKey() + HEADER_DELIMITER + header.getValue() + "\n");
        }
        for (Cookie cookie : cookies) {
            dos.writeBytes("Set-Cookie" + HEADER_DELIMITER + cookie.toString() + "\n");
        }
        dos.writeBytes("\n");
        if (body != null && body.length > 0) {
            dos.write(body, 0, body.length);
            dos.writeBytes("\n");
        }
        dos.flush();
    }
}
