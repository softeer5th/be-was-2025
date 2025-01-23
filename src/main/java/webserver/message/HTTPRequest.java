package webserver.message;

import util.HeterogeneousContainer;
import webserver.enumeration.HTTPVersion;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class HTTPRequest {
    private String method;
    private String uri;
    private HTTPVersion version;
    private HeterogeneousContainer body;
    private HeterogeneousContainer headers;
    private HeterogeneousContainer parameters;
    private Map<String, String> cookies;

    public HTTPRequest(
            String method,
            String uri,
            HTTPVersion version,
            HeterogeneousContainer body,
            HeterogeneousContainer headers,
            HeterogeneousContainer parameters,
            Map<String, String> cookies
    ) {
        this.method = method;
        this.uri = uri;
        this.version = version;
        this.body = body;
        this.headers = headers;
        this.parameters = parameters;
        this.cookies = cookies;
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
    public HeterogeneousContainer getBody() {
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
    public <T> Optional<T> getBody(String name, Class<T> type) { return body.get(name, type); }
    public Optional<String> getCookie(String name) {
        return Optional.ofNullable(this.cookies.get(name));
    }
    public static class Builder {
        private String method;
        private String uri;
        private HTTPVersion version;
        private HeterogeneousContainer headers = new HeterogeneousContainer(new LinkedHashMap<>());
        private HeterogeneousContainer parameters = new HeterogeneousContainer(new LinkedHashMap<>());
        private HeterogeneousContainer body = new HeterogeneousContainer(new LinkedHashMap<>());
        private Map<String, String> cookies = new LinkedHashMap<>();

        public Builder() {}
        public Builder method(String method) {
            this.method = method;
            return this;
        }
        public Builder uri(String uri) {
            this.uri = uri;
            return this;
        }
        public Builder version(String version) {
            this.version = HTTPVersion.from(version);
            return this;
        }
        public Builder body(HeterogeneousContainer body) {
            this.body = body;
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
        public Builder cookies(Map<String, String> cookies) {
            this.cookies = cookies;
            return this;
        }
        public HTTPRequest build() {
            return new HTTPRequest(method, uri, version, body, headers, parameters, cookies);
        }
        public String getMethod() {
            return method;
        }
    }
}
