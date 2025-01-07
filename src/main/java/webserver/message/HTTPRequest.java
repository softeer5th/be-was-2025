package webserver.message;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class HTTPRequest {
    private String method;
    private String uri;
    private HTTPVersion version;
    private Optional<String> body;
    private Map<String, String> headers;

    public HTTPRequest(
            String method,
            String uri,
            HTTPVersion version,
            Optional<String> body,
            Map<String, String> headers
    ) {
        this.method = method;
        this.uri = uri;
        this.version = version;
        this.body = body;
        this.headers = headers;
    }

    public String getMethod() {
        return method;
    }
    public String getUri() {
        return uri;
    }
    public HTTPVersion getVersion() {
        return version;
    }
    public Optional<String> getBody() {
        return body;
    }
    public Map<String, String> getHeaders() {
        return headers;
    }

    public static class Builder {
        private String method;
        private String uri;
        private HTTPVersion version;
        private Optional<String> body;
        private Map<String, String> headers = new LinkedHashMap<>();

        public Builder() {
            this.body = Optional.empty();
        }
        public Builder method(String method) {
            this.method = method;
            return this;
        }
        public Builder uri(String uri) {
            this.uri = uri;
            return this;
        }
        public Builder version(String version) {
            Objects.requireNonNull(version, "version must not be null");
            if (version.equals("HTTP/1.0")) {
                this.version = HTTPVersion.HTTP_1_0;
            } else if (version.equals("HTTP/1.1")) {
                this.version = HTTPVersion.HTTP_1_1;
            } else if (version.equals("HTTP/2")) {
                this.version = HTTPVersion.HTTP_2;
            } else {
                throw new IllegalArgumentException("invalid version: " + version);
            }
            return this;
        }
        public Builder body(String body) {
            this.body = Optional.ofNullable(body);
            return this;
        }
        public Builder setHeader(String name, String value) {
            this.headers.put(name, value);
            return this;
        }

        public HTTPRequest build() {
            return new HTTPRequest(method, uri, version, body, headers);
        }
    }
}
