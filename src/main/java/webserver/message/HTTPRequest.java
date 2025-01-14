package webserver.message;

import util.HeterogeneousContainer;
import webserver.enumeration.HTTPVersion;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;

public class HTTPRequest {
    private String method;
    private String uri;
    private HTTPVersion version;
    private Optional<String> body;
    private HeterogeneousContainer headers;
    private HeterogeneousContainer parameters;

    public HTTPRequest(
            String method,
            String uri,
            HTTPVersion version,
            Optional<String> body,
            HeterogeneousContainer headers,
            HeterogeneousContainer parameters
    ) {
        this.method = method;
        this.uri = uri;
        this.version = version;
        this.body = body;
        this.headers = headers;
        this.parameters = parameters;
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
    public Optional<String> getHeader(String name) {
        return headers.get(name, String.class);
    }
    public <T> Optional<T> getHeader(String name, Class<T> type) {
        return headers.get(name, type);
    }

    public <T> Optional<T> getParameter(String name, Class<T> type) {
        return parameters.get(name, type);
    }

    public static class Builder {
        private String method;
        private String uri;
        private HTTPVersion version;
        private Optional<String> body;
        private HeterogeneousContainer headers = new HeterogeneousContainer(new LinkedHashMap<>());
        private HeterogeneousContainer parameters = new HeterogeneousContainer(new LinkedHashMap<>());

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
        public Builder setHeaders(HeterogeneousContainer headers) {
            this.headers = headers;
            return this;
        }
        public Builder setParameters(HeterogeneousContainer parameters) {
            this.parameters = parameters;
            return this;
        }
        public HTTPRequest build() {
            return new HTTPRequest(method, uri, version, body, headers, parameters);
        }
    }
}
