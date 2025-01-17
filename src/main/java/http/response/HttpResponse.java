package http.response;

import http.enums.ContentType;
import http.enums.ErrorMessage;
import http.enums.HttpResponseStatus;
import http.enums.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
    private final HttpResponseStatus status;
    private final Map<String, String> headers;
    private final String body;

    private HttpResponse(Builder builder) {
        this.status = builder.status;
        this.headers = builder.headers;
        this.body = builder.body;
    }

    public void send(OutputStream out) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);

        dos.writeBytes(HttpVersion.HTTP_1_1.getVersion() + " " + status.getStatusCode() + " " + status.getStatusMessage() + "\r\n");

        for (Map.Entry<String, String> header : headers.entrySet()) {
            dos.writeBytes(header.getKey() + ": " + header.getValue() + "\r\n");
        }

        dos.writeBytes("\r\n");

        if (body != null && !body.isEmpty()) {
            dos.write(body.getBytes("UTF-8"));
        }
        dos.flush();
    }

    public static class Builder {
        private HttpResponseStatus status;
        private final Map<String, String> headers = new HashMap<>();
        private String body;

        public Builder() {}

        public Builder status(HttpResponseStatus status) {
            this.status = status;
            return this;
        }

        public Builder header(String key, String value) {
            headers.put(key, value);
            return this;
        }

        public Builder contentType(String contentType) {
            headers.put("Content-Type", contentType + "; charset=utf-8");
            return this;
        }

        public Builder contentLength(int length) {
            headers.put("Content-Length", String.valueOf(length));
            return this;
        }

        public Builder location(String location) {
            headers.put("Location", location);
            return this;
        }

        public Builder setCookie(Map<String, String> valueParams, Map<String, String> optionParams) {
            StringBuilder cookieBuilder = new StringBuilder();
            for (Map.Entry<String, String> entry : valueParams.entrySet()) {
                cookieBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("; ");
            }
            for (Map.Entry<String, String> entry : optionParams.entrySet()) {
                cookieBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("; ");
            }
            cookieBuilder.append("Path=/");
            headers.put("Set-Cookie", cookieBuilder.toString().trim());
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            headers.put("Content-Length", String.valueOf(body.getBytes().length));
            return this;
        }

        public Builder errorResponse(HttpResponseStatus status, ErrorMessage message) throws UnsupportedEncodingException {
            String body = String.format("<h1>%s - %s</h1>", status, message);
            return status(status)
                    .contentType(ContentType.HTML.getMimeType())
                    .contentLength(body.getBytes("UTF-8").length)
                    .body(body);
        }

        public Builder successResponse(HttpResponseStatus status, String type, String body) throws UnsupportedEncodingException {
            return status(status)
                    .contentType(type)
                    .contentLength(body.getBytes("UTF-8").length)
                    .body(body);
        }

        public Builder redirectResponse(HttpResponseStatus status, String location) {
            return status(status)
                    .contentType(ContentType.HTML.getMimeType())
                    .contentLength(0)
                    .location(location);
        }

        public HttpResponse build() {
            if (status == null) {
                throw new IllegalStateException("HTTP status must be set");
            }
            return new HttpResponse(this);
        }
    }
}